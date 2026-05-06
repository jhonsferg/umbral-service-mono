package com.codesoftlabs.umbral.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class UpdateUserDto extends CreateUserDto {
    private String mfaSecret;
    private Boolean mfaEnabled;
    private Date lastLogin;

    @Override
    @Email(message = "Email should be valid")
    public String getEmail() {
        return super.getEmail();
    }
}
