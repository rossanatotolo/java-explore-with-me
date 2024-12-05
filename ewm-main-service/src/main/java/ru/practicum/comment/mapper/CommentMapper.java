package ru.practicum.comment.mapper;

import ru.practicum.User.model.User;
import ru.practicum.comment.dto.CommentInputDto;
import ru.practicum.comment.dto.CommentOutputDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static Comment toComment(final CommentInputDto commentInputDto,
                                    final User user,
                                    final Event event) {

        return new Comment(
                null,
                commentInputDto.getText(),
                event,
                user,
                LocalDateTime.now()
        );
    }

    public static CommentOutputDto toCommentOutputDto(final Comment comment) {

        return new CommentOutputDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                EventMapper.toEventShortDto(comment.getEvent()),
                comment.getCreated()
        );
    }

    public static List<CommentOutputDto> toCommentDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentOutputDto)
                .collect(Collectors.toList());
    }
}