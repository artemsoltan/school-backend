package com.school.dto.register;

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
public class TeacherDTO {
    @NotEmpty
    @Size(min = 3, max = 64)
    private String name;

    @NotEmpty
    @Size(min = 3, max = 64)
    private String surname;

    @NotEmpty
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String date;

    @NotEmpty
    @Size(min = 4)
    private String username;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Size(min = 6)
    private String password;
}