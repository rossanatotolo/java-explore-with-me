package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentInputDto;
import ru.practicum.comment.dto.CommentOutputDto;

import java.util.List;

public interface CommentService {

    List<CommentOutputDto> getAllComments(final Long userId, final Long eventId, final int from, final int size);

    CommentOutputDto createComment(final CommentInputDto commentInputDto, final Long userId, final Long eventId);

    CommentOutputDto updateComment(final CommentInputDto commentInputDto, final Long userId, final Long commentId);

    void deleteComment(final Long userId, final Long commentId);

    List<CommentOutputDto> getAllCommentsByEvent(final Long eventId, final int from, final int size);

    CommentOutputDto getCommentById(final Long commentId);

    void deleteCommentByAdmin(final Long eventId);

    List<CommentOutputDto> searchComments(final Long userId, final Long eventId, final String text, final Integer from,
                                          final Integer size);
}