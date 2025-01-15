package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.comment.dto.CommentInputDto;
import ru.practicum.comment.dto.CommentOutputDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentOutputDto> getAllComments(final Long userId, final Long eventId, final int from, final int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} не существует." + userId));
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} не существует." + eventId));

        final List<Comment> comments = commentRepository.findByAuthorAndEvent(user, event, pageRequest);
        if (comments.isEmpty()) {
            log.info("Пользователь с id = {} еще не написал ни одного комментария к событию  с id = {}.", userId, eventId);
            return new ArrayList<>();
        }
        log.info("Получение списка комментариев пользователя  с id = {} к событию с id = {}.", userId, eventId);
        return comments.stream().map(CommentMapper::toCommentOutputDto).toList();
    }

    @Override
    public CommentOutputDto createComment(final CommentInputDto commentInputDto, final Long userId, final Long eventId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} не существует." + userId));
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} не существует." + eventId));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Невозможно написать комментарий к неопубликованному событию.");
        }

        final Comment comment = commentRepository.save(CommentMapper.toComment(commentInputDto, user, event));
        log.info("Комментарий к событию с id = {} добавлен.", eventId);
        return CommentMapper.toCommentOutputDto(comment);
    }

    @Override
    public CommentOutputDto updateComment(final CommentInputDto commentInputDto, final Long userId, final Long commentId) {
        final Comment oldComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментария с id = {} не существует." + commentId));
        userRepository.existsById(userId);

        if (!oldComment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Редактирование доступно только автору комментария.");
        }

        oldComment.setText(commentInputDto.getText());
        final Comment comment = commentRepository.save(oldComment);
        log.info("Комментарий с id = {} обновлен.", commentId);
        return CommentMapper.toCommentOutputDto(comment);
    }

    @Override
    public void deleteComment(final Long userId, final Long commentId) {
        final List<Comment> comments = commentRepository.findAll();
        final Comment comment = comments.get(Math.toIntExact(commentId));

        if (!comment.getAuthor().getId().equals(userId) &&
                !comment.getAuthor().getId().equals(comment.getEvent().getInitiator().getId())) {
            throw new ConflictException("Удаление комментария доступно только его автору или инициатору события.");
        }

        commentRepository.deleteById(commentId);
        log.info("Комментарий с id = {} удален.", commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentOutputDto> getAllCommentsByEvent(final Long eventId, final int from, final int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} не существует." + eventId));
        final List<Comment> comments = commentRepository.findByEvent(event, pageRequest);

        if (comments.isEmpty()) {
            log.info("Списка комментариев у события с id = {} не найдено.", eventId);
            return new ArrayList<>();
        }

        log.info("Получение списка комментариев события с id = {}", eventId);
        return comments.stream().map(CommentMapper::toCommentOutputDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommentOutputDto getCommentById(final Long commentId) {
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментария с id = {} не существует." + commentId));
        log.info("Получение комментария по id = {}.", commentId);
        return CommentMapper.toCommentOutputDto(comment);
    }

    @Override
    public void deleteCommentByAdmin(final Long eventId) {
        commentRepository.deleteById(eventId);
        log.info("Комментарии к событию с id = {} удалены администратором.", eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentOutputDto> searchComments(final Long userId, final Long eventId, final String text, final Integer from,
                                                 final Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        userRepository.existsById(userId);
        eventRepository.existsById(eventId);

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        final List<Comment> comments = commentRepository.findAllByAuthorIdAndEventIdAndTextContainingIgnoreCase(userId, eventId,
                text, pageRequest);

        log.info("Получение списка комментариев по текстовому запросу {}.", text);
        return CommentMapper.toCommentDtoList(comments);
    }
}
