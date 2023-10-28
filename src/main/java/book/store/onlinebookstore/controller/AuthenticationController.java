package book.store.onlinebookstore.controller;

import book.store.onlinebookstore.dto.user.UserLoginRequestDto;
import book.store.onlinebookstore.dto.user.UserLoginResponseDto;
import book.store.onlinebookstore.dto.user.UserRegistrationRequestDto;
import book.store.onlinebookstore.dto.user.UserRegistrationResponseDto;
import book.store.onlinebookstore.exception.UserRegistrationException;
import book.store.onlinebookstore.security.AuthenticationService;
import book.store.onlinebookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication controller", description = "Endpoints for authentication")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Login",
            description = "Login with email and password. Response - JWT token")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto userRequest) {
        return authenticationService.authenticate(userRequest);
    }

    @PostMapping("/register")
    @Operation(summary = "Registration",
            description = "Register a new user to the system")
    public UserRegistrationResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto userRequest)
            throws UserRegistrationException {
        return userService.register(userRequest);
    }
}
