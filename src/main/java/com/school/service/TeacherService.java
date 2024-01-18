package com.school.service;

import com.school.config.jwt.JwtUtil;
import com.school.dto.ClassDTO;
import com.school.model.Classes;
import com.school.model.Person;
import com.school.model.School;
import com.school.model.Subject;
import com.school.repository.ClassesRepository;
import com.school.repository.PersonRepository;
import com.school.repository.SchoolRepository;
import com.school.repository.SubjectRepository;
import com.school.util.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class TeacherService {
    private final PersonRepository personRepository;
    private final SubjectRepository subjectRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ClassesRepository classesRepository;
    private final SchoolRepository schoolRepository;
    
    @Autowired
    public TeacherService(PersonRepository personRepository, SubjectRepository subjectRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, ClassesRepository classesRepository, SchoolRepository schoolRepository) {
        this.personRepository = personRepository;
        this.subjectRepository = subjectRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.classesRepository = classesRepository;
        this.schoolRepository = schoolRepository;
    }

    public boolean isPersonHasSubjects(Person person) {
        if (person != null) {
            if (!person.getRole().getName().equals(RoleEnum.ROLE_TEACHER)) {
                return true;
            } else return !person.getSubjects().isEmpty();
        }

        return false;
    }

    public void setSubjects(String[] subjects, String jwt) {
        Optional<Person> teacher = personRepository.findByUsername(jwtUtil.extractUsername(jwt));
        if (teacher.isPresent()) {
            List<Subject> subjectList = new ArrayList<>();

            for (String s : subjects) {
                if (subjectRepository.findByUaName(s) != null) {
                    Subject subject = subjectRepository.findByUaName(s);
                    subjectList.add(subject);
                }
            }

            teacher.get().setSubjects(subjectList);
            personRepository.save(teacher.get());
        }
    }

    public void newClass(ClassDTO classDTO, String username) {
        Person person = personRepository.findByUsername(username).orElse(null);

        if (person != null && person.getRole().getName().equals(RoleEnum.ROLE_TEACHER)) {
            School school = schoolRepository.findByCode(person.getSchool().getCode());

            if (classesRepository.findBySchool(school) != null && classesRepository.findByName(classDTO.getName()) == null) {
                Classes classes = new Classes(classDTO.getName(), classDTO.getTeacher(), school);
                classesRepository.save(classes);
            } else if (classesRepository.findBySchool(school) == null) {
                Classes classes = new Classes(classDTO.getName(), classDTO.getTeacher(), school);
                classesRepository.save(classes);
            }
        }
    }

    public void deleteClass(String username, String className) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (person != null && person.getRole().getName().equals(RoleEnum.ROLE_TEACHER)) {
            Classes classes = classesRepository.findByNameAndSchool(className, person.getSchool()).orElse(null);
            if (classes != null) {
                classesRepository.deleteById(classes.getId());
            }
        } else {
            throw new AccessDeniedException("You don`t have permission!");
        }
    }

    public List<String> getAllTeachers(String username) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (person != null) {
            List<Person> people = personRepository.findAllBySchool(person.getSchool()).orElse(null);
            if (people != null) {
                List<String> names = new ArrayList<>();
                for (Person value : people) {
                    names.add(value.getName() + " " + value.getSurname());
                }
                return names;
            }
        }
        return null;
    }

    public List<String> getAllClasses(String username) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (person != null && person.getRole().getName().equals(RoleEnum.ROLE_TEACHER)) {
            List<Classes> classesList = classesRepository.findAllBySchool(person.getSchool());
            if (classesList != null) {
                List<String> list = new ArrayList<>();
                for (Classes classes : classesList) {
                    list.add(classes.getName());
                }
                return list;
            }
        }
        return null;
    }

    private String passwordGenerator() {
        StringBuilder password = new StringBuilder();
        String characters = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();

        password.append((random.nextInt(900000)+100000));

        for (int i = 0; i < 2; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }

        return password.toString();
    }
}