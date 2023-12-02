package com.school.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TeacherRegistrationDTO {
    private String name;
    private String surname;
    private String date;
    private String username;
    private String password;
    private String code;

    @Override
    public String toString() {
        return "PersonDTO {" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", date='" + date + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}