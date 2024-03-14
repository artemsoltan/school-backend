package com.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "date")
    private String date;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    @JsonIgnore
    private String password;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "code_id")
    @JsonIgnore
    School school;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @JsonIgnore
    Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "person_subject",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private List<Subject> subjects;

    @ManyToOne
    @JoinColumn(name = "classes_id")
    private SchoolClass studentClass;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private List<SchoolClass> teacherClasses;

    public Person(String name, String surname, String date, String username, String password, String email, Role role, School school, SchoolClass studentClass) {
        this.name = name;
        this.surname = surname;
        this.date = date;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.school = school;
        this.studentClass = studentClass;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", date='" + date + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", school=" + school +
                ", role=" + role +
                ", subjects=" + subjects +
                ", studentClass=" + studentClass +
                ", teacherClasses=" + teacherClasses +
                '}';
    }
}