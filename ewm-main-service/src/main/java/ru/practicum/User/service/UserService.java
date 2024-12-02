package ru.practicum.User.service;

import ru.practicum.User.dto.UserDtoInput;
import ru.practicum.User.dto.UserDtoOutput;

import java.util.List;

public interface UserService {
    List<UserDtoOutput> getAllUsers(List<Long> ids, final int from, final int size);

    UserDtoOutput createUser(final UserDtoInput userDtoInput);

    void deleteUser(final Long userId);
}
