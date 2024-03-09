package com.school.dto;

import com.school.model.Person;
import com.school.model.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class MarkDTO {
    private Person student;
    private int mark;
    private String type;
    private Subject subject;
    private Person teacher;
    private Date date;
}