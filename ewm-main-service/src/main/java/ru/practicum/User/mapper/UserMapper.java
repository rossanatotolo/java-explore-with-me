package ru.practicum.User.mapper;

import ru.practicum.User.dto.UserDtoInput;
import ru.practicum.User.dto.UserDtoOutput;
import ru.practicum.User.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserDtoOutput toUserDto(final User user) {
        final UserDtoOutput userDtoOutput = new UserDtoOutput();

        userDtoOutput.setId(user.getId());
        userDtoOutput.setName(user.getName());
        userDtoOutput.setEmail(user.getEmail());

        return userDtoOutput;
    }

    public static User toUser(final UserDtoInput userDtoInput) {
        final User user = new User();

        user.setName(userDtoInput.getName());
        user.setEmail(userDtoInput.getEmail());

        return user;
    }

    public static List<UserDtoOutput> toListDto(Iterable<User> users) {
        List<UserDtoOutput> result = new ArrayList<>();

        for (User user : users) {
            result.add(toUserDto(user));
        }
        return result;
    }
}
