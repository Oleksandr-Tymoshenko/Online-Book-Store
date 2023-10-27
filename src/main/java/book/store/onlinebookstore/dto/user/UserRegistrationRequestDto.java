package book.store.onlinebookstore.dto.user;

import book.store.onlinebookstore.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@FieldMatch(fields = {"password", "repeatPassword"})
public record UserRegistrationRequestDto(
        @Email(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$") @Length(max = 255)
        String email,
        @NotBlank @Length(min = 2, max = 255)
        String firstName,
        @NotBlank @Length(min = 2, max = 255)
        String lastName,
        @NotBlank @Length(min = 2, max = 255)
        String shippingAddress,
        @NotBlank @Length(min = 8, max = 255)
        String password,
        @NotBlank @Length(min = 8, max = 255)
        String repeatPassword
) {
}
