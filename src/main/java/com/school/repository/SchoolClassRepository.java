package com.school.repository;

import com.school.model.SchoolClass;
import com.school.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Integer> {
    List<SchoolClass> findBySchool(School school);
    SchoolClass findByName(String name);
    List<SchoolClass> findAllBySchool(School school);
    Optional<SchoolClass> findByNameAndSchool(String name, School school);
}