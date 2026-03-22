package dev.sweetme.repository;

import dev.sweetme.domain.Review;
import dev.sweetme.domain.enums.CareerLevel;
import dev.sweetme.domain.enums.ReviewJobCategory;
import dev.sweetme.domain.enums.ReviewStatus;
import dev.sweetme.domain.enums.ReviewType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMemberUsernameOrderByCreatedAtDesc(String memberUsername);

    @Query("SELECT r FROM Review r WHERE " +
           "(:type IS NULL OR r.type = :type) " +
           "AND (:status IS NULL OR r.status = :status) " +
           "AND (:jobCategory IS NULL OR r.jobCategory = :jobCategory) " +
           "AND (:careerLevel IS NULL OR r.careerLevel = :careerLevel) " +
           "AND (:keyword IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(r.authorName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY r.createdAt DESC")
    Page<Review> search(
            @Param("type") ReviewType type,
            @Param("status") ReviewStatus status,
            @Param("jobCategory") ReviewJobCategory jobCategory,
            @Param("careerLevel") CareerLevel careerLevel,
            @Param("keyword") String keyword,
            Pageable pageable);
}
