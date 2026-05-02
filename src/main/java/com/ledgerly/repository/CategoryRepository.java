package com.ledgerly.repository;

import com.ledgerly.domain.Category;
import com.ledgerly.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserOrUserIsNull(User user);

    Optional<Category> findByIdAndUser(Long id, User user);

    @Query("SELECT c FROM Category c WHERE c.id = :id AND (c.user = :user OR c.user IS NULL)")
    Optional<Category> findByIdAndUserOrDefault(@Param("id") Long id, @Param("user") User user);

    boolean existsByUserIsNull();
}
