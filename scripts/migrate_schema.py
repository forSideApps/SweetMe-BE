"""
INTERVIEW 스키마 → WITHSTUDY 스키마 데이터 마이그레이션
"""
import oracledb

WALLET = "/Users/goodgid/Desktop/goodGid/Spring/SideApp/WithStudy/BE/wallet"
DSN    = "clouddb_high"
PW     = "COcoding13@$"
WALLET_PW = "Cocoding13@$"

SRC_USER = "INTERVIEW"
DST_USER = "WITHSTUDY"

# 삭제/삽입 순서 (FK 의존성 고려)
TABLES = [
    "COMPANY",
    "ROOM",
    "ROOM_APPLICATION",
    "COMMUNITY_POST",
    "COMMUNITY_COMMENT",
    "REVIEW",
    "REVIEW_COMMENT",
    "VISITOR_LOG",
]

# 각 테이블 시퀀스 이름 (Hibernate 기본값)
SEQ_MAP = {
    "COMPANY":           "SEQ_COMPANY",
    "ROOM":              "SEQ_ROOM",
    "ROOM_APPLICATION":  "SEQ_ROOM_APPLICATION",
    "COMMUNITY_POST":    "SEQ_COMMUNITY_POST",
    "COMMUNITY_COMMENT": "SEQ_COMMUNITY_COMMENT",
    "REVIEW":            "SEQ_REVIEW",
    "REVIEW_COMMENT":    "SEQ_REVIEW_COMMENT",
    "VISITOR_LOG":       "SEQ_VISITOR_LOG",
}

def connect(user):
    return oracledb.connect(
        user=user, password=PW, dsn=DSN,
        config_dir=WALLET,
        wallet_location=WALLET,
        wallet_password=WALLET_PW
    )

def get_columns(cur, user, table):
    cur.execute(
        "SELECT column_name FROM all_tab_columns WHERE owner = :1 AND table_name = :2 ORDER BY column_id",
        [user.upper(), table.upper()]
    )
    return [r[0] for r in cur.fetchall()]

def table_exists(cur, user, table):
    cur.execute(
        "SELECT COUNT(*) FROM all_tables WHERE owner = :1 AND table_name = :2",
        [user.upper(), table.upper()]
    )
    return cur.fetchone()[0] > 0

def has_identity_col(cur, user, table):
    cur.execute(
        "SELECT COUNT(*) FROM all_tab_columns WHERE owner = :1 AND table_name = :2 AND identity_column = 'YES'",
        [user.upper(), table.upper()]
    )
    return cur.fetchone()[0] > 0

def seq_exists(cur, user, seq):
    cur.execute(
        "SELECT COUNT(*) FROM all_sequences WHERE sequence_owner = :1 AND sequence_name = :2",
        [user.upper(), seq.upper()]
    )
    return cur.fetchone()[0] > 0

def main():
    print(f"[연결] {SRC_USER} → {DST_USER}")
    src_conn = connect(SRC_USER)
    dst_conn = connect(DST_USER)
    src_cur  = src_conn.cursor()
    dst_cur  = dst_conn.cursor()

    # 소스 데이터 미리 읽기
    src_data = {}
    src_cols = {}
    for table in TABLES:
        if not table_exists(src_cur, SRC_USER, table):
            print(f"[SKIP] {table} (INTERVIEW에 없음)")
            continue
        cols = get_columns(src_cur, SRC_USER, table)
        col_list = ", ".join(cols)
        src_cur.execute(f"SELECT {col_list} FROM {SRC_USER}.{table}")
        src_data[table] = src_cur.fetchall()
        src_cols[table] = cols
        print(f"[READ] {table}: {len(src_data[table])}건")

    # 1단계: 역순으로 삭제 (FK 제약 고려)
    print("\n[삭제 - 역순]")
    for table in reversed(TABLES):
        if not table_exists(dst_cur, DST_USER, table):
            continue
        dst_cur.execute(f"DELETE FROM {DST_USER}.{table}")
        print(f"  {table}: {dst_cur.rowcount}건 삭제")
    dst_conn.commit()

    # 2단계: 순서대로 삽입
    print("\n[삽입]")
    for table in TABLES:
        if table not in src_data or not src_data[table]:
            continue
        if not table_exists(dst_cur, DST_USER, table):
            print(f"[SKIP] {table} (WITHSTUDY에 없음)")
            continue
        cols = src_cols[table]
        col_list = ", ".join(cols)
        bind_list = ", ".join([f":{i+1}" for i in range(len(cols))])
        # identity 컬럼이 있으면 ID 제외 (FK 참조 없는 테이블만 해당)
        if has_identity_col(dst_cur, DST_USER, table):
            id_col_idx = next((i for i, c in enumerate(cols) if c.upper() == "ID"), None)
            if id_col_idx is not None:
                cols_no_id = [c for c in cols if c.upper() != "ID"]
                col_list   = ", ".join(cols_no_id)
                bind_list  = ", ".join([f":{i+1}" for i in range(len(cols_no_id))])
                insert_sql = f"INSERT INTO {DST_USER}.{table} ({col_list}) VALUES ({bind_list})"
                rows_no_id = [tuple(v for j, v in enumerate(row) if j != id_col_idx)
                              for row in src_data[table]]
                dst_cur.executemany(insert_sql, rows_no_id)
            else:
                dst_cur.executemany(
                    f"INSERT INTO {DST_USER}.{table} ({col_list}) VALUES ({bind_list})",
                    src_data[table]
                )
        else:
            dst_cur.executemany(
                f"INSERT INTO {DST_USER}.{table} ({col_list}) VALUES ({bind_list})",
                src_data[table]
            )
        print(f"  {table}: {len(src_data[table])}건 삽입")

    dst_conn.commit()

    # 시퀀스 재설정 (max ID + 100 으로 올려서 충돌 방지)
    print("\n[시퀀스 재설정]")
    for table, seq in SEQ_MAP.items():
        if not table_exists(src_cur, SRC_USER, table):
            continue
        if not seq_exists(dst_cur, DST_USER, seq):
            print(f"[SKIP] {seq} (없음)")
            continue

        src_cur.execute(f"SELECT NVL(MAX(ID), 0) FROM {SRC_USER}.{table}")
        max_id = src_cur.fetchone()[0]
        next_val = max_id + 100

        # 현재 시퀀스 값 조회
        dst_cur.execute(
            "SELECT last_number FROM all_sequences WHERE sequence_owner = :1 AND sequence_name = :2",
            [DST_USER.upper(), seq.upper()]
        )
        row = dst_cur.fetchone()
        if not row:
            print(f"[SKIP] {seq}")
            continue
        current = row[0]

        if next_val > current:
            increment = next_val - current
            dst_cur.execute(f"ALTER SEQUENCE {DST_USER}.{seq} INCREMENT BY {increment}")
            dst_cur.execute(f"SELECT {DST_USER}.{seq}.NEXTVAL FROM DUAL")
            dst_cur.execute(f"ALTER SEQUENCE {DST_USER}.{seq} INCREMENT BY 1")
            dst_conn.commit()
            print(f"[OK] {seq}: {current} → {next_val}")
        else:
            print(f"[OK] {seq}: 이미 충분함 ({current})")

    src_conn.close()
    dst_conn.close()
    print("\n마이그레이션 완료!")

if __name__ == "__main__":
    main()
