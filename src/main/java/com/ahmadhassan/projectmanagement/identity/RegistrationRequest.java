package com.ahmadhassan.projectmanagement.identity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(

        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @NotNull
        @Size(min = 12, max = 64)
        String password
) {

    public RegistrationRequest {
        if (email != null) {
            email = email.trim();
        }
    }
}