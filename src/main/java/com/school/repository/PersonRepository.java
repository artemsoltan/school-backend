package com.school.repository;

import com.school.model.Role;
import com.school.model.SchoolClass;
import com.school.model.Person;
import com.school.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByUsername(String username);
    Optional<Person> findById(int id);
    Optional<List<Person>> findAllBySchool(School school);
    Optional<List<Person>> findAllBySchoolAndRole(School school, Role role);
    Optional<List<Person>> findAllByStudentClass(SchoolClass schoolClass);
    Optional<Person> findByEmail(String email);
}