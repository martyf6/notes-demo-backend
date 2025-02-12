package com.jfahey.notes.model.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class UserAPI {

    @NotNull
    @Length(min = 5, max = 30)
    private String username;

    @NotNull
    @Email
    @Length(min = 5, max = 50)
    private String email;

    @NotNull
    @Length(min = 5, max = 50)
    private String password;

    @NotNull
    @Length(min = 5, max = 50)
    private String confirmPassword;
}
