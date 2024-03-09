package com.school.service;

import com.school.config.jwt.JwtUtil;
import com.school.dto.ClassDTO;
import com.school.model.SchoolClass;
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

@Service
public class TeacherService {
    private final PersonRepository personRepository;
    private final SubjectRepository subjectRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ClassesRepository classesRepository;
    private final SchoolRepository schoolRepository;
    private final SaveUserService saveUserService;
    
    @Autowired
    public TeacherService(PersonRepository personRepository, SubjectRepository subjectRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, ClassesRepository classesRepository, SchoolRepository schoolRepository, SaveUserService saveUserService) {
        this.personRepository = personRepository;
        this.subjectRepository = subjectRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.classesRepository = classesRepository;
        this.schoolRepository = schoolRepository;
        this.saveUserService = saveUserService;
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

        if (isPersonTeacher(person) && person != null) {
            School school = schoolRepository.findByCode(person.getSchool().getCode());
            if (classesRepository.findBySchool(school) != null && classesRepository.findByName(classDTO.getName()) == null) {
                SchoolClass schoolClass = new SchoolClass(classDTO.getName(), person, school);
                classesRepository.save(schoolClass);
            } else if (classesRepository.findBySchool(school) == null) {
                SchoolClass schoolClass = new SchoolClass(classDTO.getName(), person, school);
                classesRepository.save(schoolClass);
            }
        }
    }

    public void deleteClass(String username, String className) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (isPersonTeacher(person)) {
            SchoolClass schoolClass = classesRepository.findByNameAndSchool(className, person.getSchool()).orElse(null);
            if (schoolClass != null) {
                classesRepository.deleteById(schoolClass.getId());
                List<Person> students = personRepository.findAllByStudentClass(schoolClass).orElse(null);
                if (students != null) {
                    personRepository.deleteAll(students);
                }
            }
        } else {
            throw new AccessDeniedException("You don`t have permission!");
        }
    }

    public List<String> getAllTeachers(String username) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (person != null) {
            List<Person> teachers = personRepository.findAllBySchool(person.getSchool()).orElse(null);
            if (teachers != null) {
                List<String> names = new ArrayList<>();
                for (Person teacher : teachers) {
                    if (isPersonTeacher(teacher)) {
                        names.add(teacher.getName() + " " + teacher.getSurname());
                    }
                }
                return names;
            }
        }
        return null;
    }

    public List<SchoolClass> getAllClasses(String username) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (isPersonTeacher(person)) {
            List<SchoolClass> schoolClassList = classesRepository.findAllBySchool(person.getSchool());
            if (schoolClassList != null) {
                return schoolClassList;
            }
        }
        return null;
    }

    public boolean doesTeacherBelongsToTheClass(int id, String username) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (person != null) {
            if (isPersonTeacher(person)) {
                SchoolClass schoolClass = classesRepository.findById(id).orElse(null);
                return schoolClass != null && person.getTeacherClasses().contains(schoolClass);
            }
        }
        return false;
    }

    public List<Person> getAllStudentsByTeacher(String username, int classId) {
        Person teacher = personRepository.findByUsername(username).orElse(null);
        if (isPersonTeacher(teacher) && teacher.getTeacherClasses().contains(classesRepository.findById(classId).get())) {
            return personRepository.findAllByStudentClass(classesRepository.findById(classId).orElse(null)).orElse(null);
        }
        return null;
    }

    public boolean isPersonTeacher(Person person) {
        return person != null && person.getRole().getName().equals(RoleEnum.ROLE_TEACHER);
    }
}