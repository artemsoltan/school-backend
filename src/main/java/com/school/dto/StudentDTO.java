package com.school.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    @NotEmpty
    @Size(min = 4)
    private String username;

    @NotEmpty
    @Size(min = 8)
    private String password;

    @NotEmpty
    private String name;

    @NotEmpty
    private String surname;

    @NotEmpty
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String date;

    @NotEmpty
    @Email
    private String email;
}