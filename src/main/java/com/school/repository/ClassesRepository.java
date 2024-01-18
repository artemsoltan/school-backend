package com.school.repository;

import com.school.model.Classes;
import com.school.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassesRepository extends JpaRepository<Classes, Integer> {
    List<Classes> findBySchool(School school);
    List<Classes> findByName(String name);
    List<Classes> findAllBySchool(School school);
    Optional<Classes> findByNameAndSchool(String name, School school);
}