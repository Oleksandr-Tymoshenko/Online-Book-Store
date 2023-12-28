package book.store.onlinebookstore.user;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.onlinebookstore.dto.user.UserLoginRequestDto;
import book.store.onlinebookstore.dto.user.UserLoginResponseDto;
import book.store.onlinebookstore.dto.user.UserRegistrationRequestDto;
import book.store.onlinebookstore.dto.user.UserRegistrationResponseDto;
import book.store.onlinebookstore.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {
    private static MockMvc mockMvc;
    private static final String DEFAULT_USERNAME = "email@gmail.com";
    private static final String DEFAULT_PASSWORD = "12345678";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Check if login returns jwt token")
    @Sql(scripts = "classpath:database.scripts/user/add-user-with-encr-pass.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/user/delete-user.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @SneakyThrows
    void login_ValidCredentials_ReturnsJwtToken() {
        //given
        UserLoginRequestDto requestDto = new UserLoginRequestDto(DEFAULT_USERNAME,
                DEFAULT_PASSWORD);

        //when
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        var actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserLoginResponseDto.class);
        Assertions.assertTrue(jwtUtil.isValidToken(actual.token()));
    }

    @Test
    @DisplayName("Check user registration")
    @Sql(scripts = "classpath:database.scripts/user/delete-user.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @SneakyThrows
    void register_ValidUserRequest_ReturnsUserDto() {
        //given
        var requestDto = new UserRegistrationRequestDto(
                DEFAULT_USERNAME,
                "Name",
                "Surname",
                "Address",
                DEFAULT_PASSWORD,
                DEFAULT_PASSWORD
        );

        var expected = new UserRegistrationResponseDto(
                1L,
                requestDto.email(),
                requestDto.firstName(),
                requestDto.lastName(),
                requestDto.shippingAddress()
        );

        //when
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        var actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserRegistrationResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }
}
