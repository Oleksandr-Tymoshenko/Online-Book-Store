package book.store.onlinebookstore.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import book.store.onlinebookstore.model.User;
import book.store.onlinebookstore.repository.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    private static final String DEFAULT_USERNAME = "email@gmail.com";
    private static final String DEFAULT_PASSWORD = "12345678";
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Check if findUserByEmail returns correct user")
    @Sql(scripts = "classpath:database.scripts/user/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/user/delete-user.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findUserByEmail_ValidEmail_ReturnsUser() {
        //given
        User expected = new User();
        expected.setId(1L);
        expected.setEmail(DEFAULT_USERNAME);
        expected.setPassword(DEFAULT_PASSWORD);
        expected.setFirstName("name");
        expected.setLastName("surname");
        expected.setShippingAddress("address");

        //when
        Optional<User> actual = userRepository.findUserByEmail(expected.getEmail());

        //then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }
}
