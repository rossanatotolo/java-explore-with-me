package ru.practicum.user.service;

import ru.practicum.user.dto.UserDtoInput;
import ru.practicum.user.dto.UserDtoOutput;

import java.util.List;

public interface UserService {
    List<UserDtoOutput> getAllUsers(List<Long> ids, final int from, final int size);

    UserDtoOutput createUser(final UserDtoInput userDtoInput);

    void deleteUser(final Long userId);
}
