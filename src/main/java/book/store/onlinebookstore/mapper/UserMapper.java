package book.store.onlinebookstore.mapper;

import book.store.onlinebookstore.dto.user.UserRegistrationRequestDto;
import book.store.onlinebookstore.dto.user.UserRegistrationResponseDto;
import book.store.onlinebookstore.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface UserMapper {
    UserRegistrationResponseDto toUserResponseDto(User user);

    User toUser(UserRegistrationRequestDto requestDto);
}
