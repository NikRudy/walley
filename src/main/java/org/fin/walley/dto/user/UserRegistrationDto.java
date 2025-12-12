package org.fin.walley.dto.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDto {


    @NotBlank(message = "{user.registration.username.not-blank}")
    @Size(min = 3, max = 50, message = "{user.registration.username.size}")
    private String username;


    @NotBlank(message = "{user.registration.email.not-blank}")
    @Email(message = "{user.registration.email.invalid}")
    private String email;


    @NotBlank(message = "{user.registration.password.not-blank}")
    @Size(min = 8, max = 100, message = "{user.registration.password.size}")
    private String password;


    @NotBlank(message = "{user.registration.confirm-password.not-blank}")
    @Size(min = 8, max = 100, message = "{user.registration.confirm-password.size}")
    private String confirmPassword;
}