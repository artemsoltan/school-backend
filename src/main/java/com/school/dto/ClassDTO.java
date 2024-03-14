package com.school.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassDTO {
    private String name;
    private String teacherUsername;

    @Override
    public String toString() {
        return "ClassDTO{" +
                "name='" + name + '\'' +
                ", teacherUsername='" + teacherUsername + '\'' +
                '}';
    }
}