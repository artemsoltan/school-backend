package com.school.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "teacher")
    private String teacher;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    public Classes(String name, String teacher, School school) {
        this.name = name;
        this.teacher = teacher;
        this.school = school;
    }
}