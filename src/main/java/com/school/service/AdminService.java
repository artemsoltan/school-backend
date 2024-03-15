package com.school.service;

import com.school.config.jwt.JwtUtil;
import com.school.dto.ClassDTO;
import com.school.exception.NotAccessException;
import com.school.model.Person;
import com.school.model.Role;
import com.school.model.School;
import com.school.model.SchoolClass;
import com.school.repository.*;
import com.school.service.impl.AdminServiceImpl;
import com.school.service.impl.SchoolServiceImpl;
import com.school.util.RoleEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService implements AdminServiceImpl, SchoolServiceImpl {
    private final PersonRepository personRepository;
    private final SubjectRepository subjectRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final SchoolClassRepository schoolClassRepository;
    private final SchoolRepository schoolRepository;
    private final SaveUserService saveUserService;
    private final RoleRepository roleRepository;

    public AdminService(PersonRepository personRepository, SubjectRepository subjectRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, SchoolClassRepository schoolClassRepository, SchoolRepository schoolRepository, SaveUserService saveUserService, RoleRepository roleRepository) {
        this.personRepository = personRepository;
        this.subjectRepository = subjectRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.schoolClassRepository = schoolClassRepository;
        this.schoolRepository = schoolRepository;
        this.saveUserService = saveUserService;
        this.roleRepository = roleRepository;
    }

    public void newClass(ClassDTO classDTO, String username) {
        Person admin = personRepository.findByUsername(username).orElse(null);

        if (isPersonAdmin(admin) && admin != null) {
            School school = schoolRepository.findByCode(admin.getSchool().getCode());
            Person teacher = personRepository.findByUsername(classDTO.getTeacherUsername()).orElse(null);
            if (teacher != null && (teacher.getRole().getName().equals(RoleEnum.ROLE_TEACHER) || teacher.getRole().getName().equals(RoleEnum.ROLE_ADMIN)) && teacher.getSchool().equals(admin.getSchool())) {
                if (schoolClassRepository.findByNameAndSchool(classDTO.getName(), school).isEmpty()) {
                    SchoolClass schoolClass = new SchoolClass(
                            classDTO.getName(),
                            teacher,
                            school
                    );
                    schoolClassRepository.save(schoolClass);
                }
            } else {
                throw new NotAccessException();
            }
        } else {
            throw new NotAccessException();
        }
    }

    public void deleteClass(String username, String className) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (isPersonAdmin(person)) {
            SchoolClass schoolClass = schoolClassRepository.findByNameAndSchool(className, person.getSchool()).orElse(null);
            if (schoolClass != null) {
                schoolClassRepository.deleteById(schoolClass.getId());
                List<Person> students = personRepository.findAllByStudentClass(schoolClass).orElse(null);
                if (students != null) {
                    personRepository.deleteAll(students);
                }
            }
        } else {
            throw new NotAccessException();
        }
    }

    public List<Person> getAllTeachers(String username) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (person != null) {
            Role role = roleRepository.findByName(RoleEnum.ROLE_TEACHER);
            return personRepository.findAllBySchoolAndRole(person.getSchool(), role).orElse(null);
        }
        return null;
    }

    public List<SchoolClass> getAllClasses(String username) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (person != null && isPersonAdmin(person)) {
            return schoolClassRepository.findAllBySchool(person.getSchool());
        }
        return null;
    }

    public boolean doesAdminBelongsToTheSchool(int id, String username) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (person != null) {
            if (isPersonAdmin(person)) {
                School school = schoolRepository.findById(id);
                return person.getSchool().equals(school);
            }
        }
        return false;
    }

    public List<Person> getAllStudentsByTeacher(String username, int classId) {
        Person admin = personRepository.findByUsername(username).orElse(null);
        if (isPersonAdmin(admin)) {
            return personRepository.findAllByStudentClass(schoolClassRepository.findById(classId).orElse(null)).orElse(null);
        }
        return null;
    }

    public boolean isPersonAdmin(Person person) {
        return person != null && person.getRole().getName().equals(RoleEnum.ROLE_ADMIN);
    }
}