package com.school.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {
    @NotEmpty(message = "Введіть будь ласка своє ім'я!")
    private String name;

    @NotEmpty(message = "Введіть будь ласка своє прізвище!")
    private String surname;

    @NotEmpty(message = "Веедіть своє день народження корректно!")
    private String date;

    @NotEmpty(message = "Введіть будь ласка свій логін!")
    private String username;

    @NotEmpty(message = "Введіть будь ласка свій пароль!")
    private String password;

    @NotEmpty(message = "Введіть будь ласка спеціальний код!")
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