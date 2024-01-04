package com.school.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchoolDTO {
    private String name;
    private String shortname;
    private String city;
    private String email;

    @Override
    public String toString() {
        return "SchoolDTO{" +
                "name='" + name + '\'' +
                ", shortname='" + shortname + '\'' +
                ", city='" + city + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}