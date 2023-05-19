package com.hobbi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hobbi.model.entities.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
}
