package com.school.dto;

import com.school.model.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class MarkDTO {
    private String studentUsername;
    private int mark;
    private String type;
    private Subject subject;
    private String teacherUsername;
    private Date date;
}