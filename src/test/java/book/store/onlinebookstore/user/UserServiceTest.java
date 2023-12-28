package book.store.onlinebookstore.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import book.store.onlinebookstore.dto.user.UserRegistrationRequestDto;
import book.store.onlinebookstore.dto.user.UserRegistrationResponseDto;
import book.store.onlinebookstore.exception.UserRegistrationException;
import book.store.onlinebookstore.mapper.UserMapper;
import book.store.onlinebookstore.mapper.UserMapperImpl;
import book.store.onlinebookstore.model.Role;
import book.store.onlinebookstore.model.User;
import book.store.onlinebookstore.repository.role.RoleRepository;
import book.store.onlinebookstore.repository.user.UserRepository;
import book.store.onlinebookstore.service.impl.UserServiceImpl;
import java.util.Optional;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final String DEFAULT_USERNAME = "email@gmail.com";
    private static final String DEFAULT_PASSWORD = "12345678";
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Spy
    private PasswordEncoder passwordEncoder;
    @Spy
    private UserMapper userMapper = new UserMapperImpl();
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Check if new user is registered")
    @SneakyThrows
    void register_ValidUserRequest_ReturnsUserDto() {
        //given
        var requestDto = getDefaultUserRegistrationRequestDto();
        User user = new User();
        user.setEmail(requestDto.email());
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
        user.setShippingAddress(requestDto.shippingAddress());

        Mockito.when(userRepository.findUserByEmail(requestDto.email()))
                .thenReturn(Optional.empty());
        Mockito.when(roleRepository.getRoleByName(Role.RoleName.USER)).thenReturn(new Role());
        Mockito.when(userRepository.save(user)).thenReturn(user);
        var expected = new UserRegistrationResponseDto(
                null,
                requestDto.email(),
                requestDto.firstName(),
                requestDto.lastName(),
                requestDto.shippingAddress()
        );

        //when
        UserRegistrationResponseDto actual = userService.register(requestDto);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    @DisplayName("Check if register throws exception when user with this email exists")
    void register_InvalidUserEmail_ThrowsException() {
        //given
        var requestDto = getDefaultUserRegistrationRequestDto();
        Mockito.when(userRepository.findUserByEmail(requestDto.email()))
                .thenReturn(Optional.of(new User()));

        //when
        UserRegistrationException exception = assertThrows(
                UserRegistrationException.class,
                () -> userService.register(requestDto)
        );

        //then
        String expected = "Can't register user";
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    private static UserRegistrationRequestDto getDefaultUserRegistrationRequestDto() {
        return new UserRegistrationRequestDto(
                DEFAULT_USERNAME,
                "Name",
                "Surname",
                "Address",
                DEFAULT_PASSWORD,
                DEFAULT_PASSWORD
        );
    }
}
