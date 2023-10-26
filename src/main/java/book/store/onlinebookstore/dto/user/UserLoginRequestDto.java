package book.store.onlinebookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @Email @NotBlank @Length(max = 255)
        String email,
        @NotBlank @Length(min = 8, max = 255)
        String password) {
}
