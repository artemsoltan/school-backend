package com.school.service;

import com.school.dto.register.AdminDTO;
import com.school.dto.register.StudentDTO;
import com.school.dto.register.TeacherDTO;
import com.school.exception.NotAccessException;
import com.school.exception.InvalidCredentialsException;
import com.school.model.SchoolClass;
import com.school.model.Person;
import com.school.model.Role;
import com.school.model.School;
import com.school.repository.SchoolClassRepository;
import com.school.repository.PersonRepository;
import com.school.repository.RoleRepository;
import com.school.repository.SchoolRepository;
import com.school.service.impl.AdminServiceImpl;
import com.school.util.RoleEnum;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaveUserService implements AdminServiceImpl {
    private final RoleRepository roleRepository;
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final SchoolClassRepository schoolClassRepository;

    public SaveUserService(RoleRepository roleRepository, SchoolRepository schoolRepository, PasswordEncoder passwordEncoder, PersonRepository personRepository, SchoolClassRepository schoolClassRepository) {
        this.roleRepository = roleRepository;
        this.schoolRepository = schoolRepository;
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.schoolClassRepository = schoolClassRepository;
    }

    public void saveAdmin(AdminDTO adminDTO) {
        Role role = roleRepository.findByName(RoleEnum.ROLE_ADMIN);
        School school = schoolRepository.findByCode(adminDTO.getCode());

        Person person = new Person(adminDTO.getName(),
                                    adminDTO.getSurname(),
                                    adminDTO.getDate(),
                                    adminDTO.getUsername(),
                                    passwordEncoder.encode(adminDTO.getPassword()),
                                    adminDTO.getEmail(),
                                    role,
                                    school,
                            null
        );

        personRepository.save(person);
    }

    public void saveTeacher(TeacherDTO teacherDTO, String adminUsername) {
        Role role = roleRepository.findByName(RoleEnum.ROLE_TEACHER);
        Person admin = personRepository.findByUsername(adminUsername).orElse(null);
        if (admin != null && admin.getRole().getName().equals(RoleEnum.ROLE_ADMIN)) {
            Person person = new Person(teacherDTO.getName(),
                    teacherDTO.getSurname(),
                    teacherDTO.getDate(),
                    teacherDTO.getUsername(),
                    passwordEncoder.encode(teacherDTO.getPassword()),
                    teacherDTO.getEmail(),
                    role,
                    admin.getSchool(),
                    null
            );

            personRepository.save(person);
        } else {
            throw new NotAccessException();
        }
    }

    public void saveStudents(int id, List<StudentDTO> students, String username) {
        Person admin = personRepository.findByUsername(username).orElse(null);
        Role role = roleRepository.findByName(RoleEnum.ROLE_STUDENT);
        if (admin != null && isPersonAdmin(admin)) {
            List<Person> people = new ArrayList<>();
            for (StudentDTO student : students) {
                SchoolClass schoolClass = schoolClassRepository.findById(id).orElse(null);
                if (schoolClass != null) {
                    people.add(new Person(
                            student.getName(),
                            student.getSurname(),
                            student.getDate(),
                            student.getUsername(),
                            passwordEncoder.encode(student.getPassword()),
                            student.getEmail(),
                            role,
                            admin.getSchool(),
                            schoolClass
                    ));
                }
            }
            try {
                personRepository.saveAll(people);
            } catch (Exception e) {
                throw new InvalidCredentialsException();
            }
        } else {
            throw new NotAccessException();
        }
    }

    public boolean isPersonAdmin(Person person) {
        return person != null && person.getRole().getName().equals(RoleEnum.ROLE_ADMIN);
    }
}