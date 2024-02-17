package com.school.service;

import com.school.dto.StudentDTO;
import com.school.dto.TeacherRegistrationDTO;
import com.school.model.Classes;
import com.school.model.Person;
import com.school.model.Role;
import com.school.model.School;
import com.school.repository.ClassesRepository;
import com.school.repository.PersonRepository;
import com.school.repository.RoleRepository;
import com.school.repository.SchoolRepository;
import com.school.util.RoleEnum;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class SaveUserService {
    private final RoleRepository roleRepository;
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final ClassesRepository classesRepository;

    public SaveUserService(RoleRepository roleRepository, SchoolRepository schoolRepository, PasswordEncoder passwordEncoder, PersonRepository personRepository, ClassesRepository classesRepository) {
        this.roleRepository = roleRepository;
        this.schoolRepository = schoolRepository;
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.classesRepository = classesRepository;
    }

    public void saveTeacher(TeacherRegistrationDTO teacherRegistrationDTO) {
        Role role = roleRepository.findByName(RoleEnum.ROLE_TEACHER);
        School school = schoolRepository.findByCode(teacherRegistrationDTO.getCode());

        Person person = new Person(teacherRegistrationDTO.getName(),
                                    teacherRegistrationDTO.getSurname(),
                                    teacherRegistrationDTO.getDate(),
                                    teacherRegistrationDTO.getUsername(),
                                    passwordEncoder.encode(teacherRegistrationDTO.getPassword()),
                                    teacherRegistrationDTO.getEmail(),
                                    role,
                                    school,
                            null
        );

        personRepository.save(person);
    }

    public void saveStudents(List<StudentDTO> students, String username) {
        Person teacher = personRepository.findByUsername(username).orElse(null);
        Role role = roleRepository.findByName(RoleEnum.ROLE_STUDENT);
        if (isPersonTeacher(teacher)) {
            List<Person> people = new ArrayList<>();
            for (StudentDTO student : students) {
                Classes classes = classesRepository.findById(student.getClassId()).orElse(null);
                if (classes != null) {
                    people.add(new Person(
                            student.getName(),
                            student.getSurname(),
                            student.getDate(),
                            student.getUsername(),
                            passwordEncoder.encode(student.getPassword()),
                            student.getEmail(),
                            role,
                            teacher.getSchool(),
                            classes
                    ));
                }
            }
            personRepository.saveAll(people);
        }
    }

    public boolean isPersonTeacher(Person person) {
        return person != null && person.getRole().getName().equals(RoleEnum.ROLE_TEACHER);
    }
}