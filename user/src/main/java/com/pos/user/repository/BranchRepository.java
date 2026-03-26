package com.pos.user.repository;

import com.pos.user.entity.Branch;
import com.pos.user.entity.enums.BranchType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {

    boolean existsByCode(String code);

    List<Branch> findByType(BranchType type);

    List<Branch> findByIsActiveTrue();

    Optional<Branch> findByIdAndIsActiveTrue(Integer id);
}
