package com.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "classes")
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "school_id")
    @JsonIgnore
    private School school;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @JsonIgnore
    private Person teacher;

    public Classes(String name, Person teacher, School school) {
        this.name = name;
        this.teacher = teacher;
        this.school = school;
    }
}