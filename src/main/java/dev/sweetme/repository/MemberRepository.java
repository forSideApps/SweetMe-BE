package dev.sweetme.repository;

import dev.sweetme.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Member> findByUsername(String username);
}
