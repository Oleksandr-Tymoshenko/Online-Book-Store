package book.store.onlinebookstore.service;

import book.store.onlinebookstore.dto.user.UserRegistrationRequestDto;
import book.store.onlinebookstore.dto.user.UserRegistrationResponseDto;
import book.store.onlinebookstore.exception.UserRegistrationException;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto registrationRequest)
            throws UserRegistrationException;
}
