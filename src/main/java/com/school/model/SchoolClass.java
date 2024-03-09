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
public class SchoolClass {
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

    public SchoolClass(String name, Person teacher, School school) {
        this.name = name;
        this.teacher = teacher;
        this.school = school;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolClass schoolClass = (SchoolClass) o;
        return id == schoolClass.id && Objects.equals(name, schoolClass.name) && Objects.equals(school, schoolClass.school) && Objects.equals(teacher, schoolClass.teacher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, school, teacher);
    }
}