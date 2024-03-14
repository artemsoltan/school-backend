package com.school.service;

import com.school.config.jwt.JwtUtil;
import com.school.model.SchoolClass;
import com.school.model.Person;
import com.school.model.Subject;
import com.school.repository.SchoolClassRepository;
import com.school.repository.PersonRepository;
import com.school.repository.SchoolRepository;
import com.school.repository.SubjectRepository;
import com.school.service.impl.SchoolServiceImpl;
import com.school.service.impl.TeacherServiceImpl;
import com.school.util.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherService implements TeacherServiceImpl, SchoolServiceImpl {
    private final PersonRepository personRepository;
    private final SubjectRepository subjectRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final SchoolClassRepository schoolClassRepository;
    private final SchoolRepository schoolRepository;
    private final SaveUserService saveUserService;
    
    @Autowired
    public TeacherService(PersonRepository personRepository, SubjectRepository subjectRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, SchoolClassRepository schoolClassRepository, SchoolRepository schoolRepository, SaveUserService saveUserService) {
        this.personRepository = personRepository;
        this.subjectRepository = subjectRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.schoolClassRepository = schoolClassRepository;
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

    public List<SchoolClass> getAllClasses(String username) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (isPersonTeacher(person)) {
            List<SchoolClass> schoolClassList = schoolClassRepository.findAllBySchool(person.getSchool());
            return schoolClassList;
        }
        return null;
    }

    public boolean doesTeacherBelongsToTheClass(int id, String username) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (person != null) {
            if (isPersonTeacher(person)) {
                SchoolClass schoolClass = schoolClassRepository.findById(id).orElse(null);

                return schoolClass != null && schoolClass.getSchool().equals(person.getSchool());
            }
        }
        return false;
    }

    public List<Person> getAllStudentsByTeacher(String username, int classId) {
        Person teacher = personRepository.findByUsername(username).orElse(null);
        if (isPersonTeacher(teacher) && teacher.getTeacherClasses().contains(schoolClassRepository.findById(classId).get())) {
            return personRepository.findAllByStudentClass(schoolClassRepository.findById(classId).orElse(null)).orElse(null);
        }
        return null;
    }

    public boolean isPersonTeacher(Person person) {
        return person != null && person.getRole().getName().equals(RoleEnum.ROLE_TEACHER);
    }
}