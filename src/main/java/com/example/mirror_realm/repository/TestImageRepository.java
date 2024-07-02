package com.example.mirror_realm.repository;

import com.example.mirror_realm.model.TestImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestImageRepository extends JpaRepository<TestImage, Integer> {
}
