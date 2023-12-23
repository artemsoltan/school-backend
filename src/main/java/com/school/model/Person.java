package com.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
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
    @JsonIgnore
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

    @ManyToOne
    @JoinColumn(name = "code_id")
    @JsonIgnore
    School school;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @JsonIgnore
    Role role;

    @ManyToMany
    @JoinTable(
            name = "person_subject",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    @JsonIgnore
    private List<Subject> subjects;

    public Person(String name, String surname, String date, String username, String password, Role role, School school) {
        this.name = name;
        this.surname = surname;
        this.date = date;
        this.username = username;
        this.password = password;
        this.role = role;
        this.school = school;
    }
}