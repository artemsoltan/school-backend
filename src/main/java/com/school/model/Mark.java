package com.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "marks")
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "person_id")
    Person student;

    @Column(name = "mark")
    private int mark;

    @Column(name = "type")
    private String type;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    Person teacher;

    @Column(name = "date")
    private Date date;

    public Mark(Person student, int mark, String type, Subject subject, Person teacher, Date date) {
        this.student = student;
        this.mark = mark;
        this.type = type;
        this.subject = subject;
        this.teacher = teacher;
        this.date = date;
    }

    public Mark() {}
}