package com.school.repository;

import com.school.model.Classes;
import com.school.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassesRepository extends JpaRepository<Classes, Integer> {
    Classes findBySchool(School school);
    Classes findByName(String name);
}