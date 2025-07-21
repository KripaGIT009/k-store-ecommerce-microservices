package com.kstore.product.repository;

import com.kstore.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    List<Category> findByActiveTrue();

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findRootCategories();

    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId")
    List<Category> findByParentCategoryId(Long parentId);

    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Long id);
}
