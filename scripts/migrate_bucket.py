#!/usr/bin/env python3
"""
OCI Object Storage 마이그레이션 스크립트
WithStudy/ 폴더의 파일들을 SweetMe/ 하위로 이동합니다.

사전 요구사항:
  pip install oci

실행:
  python3 migrate_bucket.py
"""

import io
import oci

NAMESPACE     = "axw45xvpnfol"
BUCKET        = "MyBucket"
REGION        = "ap-chuncheon-1"
SOURCE_PREFIX = "WithStudy/"
TARGET_PREFIX = "SweetMe/"

def main():
    config = oci.config.from_file("~/.oci/config", "DEFAULT")
    client = oci.object_storage.ObjectStorageClient(config)

    print(f"버킷: {BUCKET} / 네임스페이스: {NAMESPACE}")
    print(f"{SOURCE_PREFIX} → {TARGET_PREFIX} 이동 시작\n")

    # 전체 객체 목록 수집 (페이지네이션 처리)
    objects = []
    next_page = None
    while True:
        resp = client.list_objects(NAMESPACE, BUCKET, start=next_page)
        objects.extend(resp.data.objects)
        if resp.data.next_start_with:
            next_page = resp.data.next_start_with
        else:
            break

    targets = [o for o in objects if o.name.startswith(SOURCE_PREFIX)]
    if not targets:
        print("이동할 파일이 없습니다. (WithStudy/ 아래에 파일 없음)")
        return

    print(f"이동 대상: {len(targets)}개\n")

    for obj in targets:
        src = obj.name
        dst = TARGET_PREFIX + src[len(SOURCE_PREFIX):]
        print(f"  {src}  →  {dst}")

        # 다운로드
        get_resp = client.get_object(NAMESPACE, BUCKET, src)
        data = get_resp.data.content
        content_type = get_resp.headers.get("content-type", "image/png")

        # 재업로드
        client.put_object(NAMESPACE, BUCKET, dst, io.BytesIO(data), content_type=content_type)

        # 원본 삭제
        client.delete_object(NAMESPACE, BUCKET, src)
        print(f"    완료")

    print(f"\n마이그레이션 완료: {len(targets)}개 파일 이동됨 (WithStudy/ → SweetMe/)")

if __name__ == "__main__":
    main()
