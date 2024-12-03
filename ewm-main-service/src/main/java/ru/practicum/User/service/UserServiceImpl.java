package ru.practicum.User.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.User.dto.UserDtoInput;
import ru.practicum.User.dto.UserDtoOutput;
import ru.practicum.User.mapper.UserMapper;
import ru.practicum.User.model.User;
import ru.practicum.User.repository.UserRepository;
import ru.practicum.exception.DuplicatedDataException;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDtoOutput> getAllUsers(List<Long> ids, final int from, final int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        log.info("Запрос на получение списка пользователей");
        final List<User> users;
        if (Objects.isNull(ids) || ids.isEmpty()) {
            users = userRepository.findAll(pageRequest).getContent();
            log.info("Получен списк всех пользователей");
        } else {
            users = userRepository.findByIdIn(ids, pageRequest);
            log.info("Получен списк пользователей по заданным id");
        }
        return UserMapper.toListDto(users);

    }

    @Override
    public UserDtoOutput createUser(final UserDtoInput userDtoInput) {
        if (userRepository.findAll().contains(UserMapper.toUser(userDtoInput))) {
            log.warn("Пользователь с id = {} уже добавлен в список.", userDtoInput.getId());
            throw new DuplicatedDataException("Этот пользователь уже существует.");
        }

        final User user = userRepository.save(UserMapper.toUser(userDtoInput));
        log.info("Пользователь с id = {} добавлен.", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(final Long userId) {
        userRepository.deleteById(userId);
        log.info("Пользователь с id  = {} удален.", userId);
    }
}
