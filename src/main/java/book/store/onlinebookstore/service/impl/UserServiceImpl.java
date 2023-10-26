package book.store.onlinebookstore.service.impl;

import book.store.onlinebookstore.dto.user.UserRegistrationRequestDto;
import book.store.onlinebookstore.dto.user.UserRegistrationResponseDto;
import book.store.onlinebookstore.exception.UserRegistrationException;
import book.store.onlinebookstore.mapper.UserMapper;
import book.store.onlinebookstore.model.Role;
import book.store.onlinebookstore.model.User;
import book.store.onlinebookstore.repository.role.RoleRepository;
import book.store.onlinebookstore.repository.user.UserRepository;
import book.store.onlinebookstore.service.UserService;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto registrationRequest)
            throws UserRegistrationException {
        if (userRepository.findUserByEmail(registrationRequest.email()).isPresent()) {
            throw new UserRegistrationException("Can't register user");
        }
        User user = userMapper.toUser(registrationRequest);
        user.setPassword(passwordEncoder.encode(registrationRequest.password()));
        user.setRoles(new HashSet<>());
        user.getRoles().add(roleRepository.getRoleByName(Role.RoleName.USER));
        return userMapper.toUserResponseDto(userRepository.save(user));
    }
}
