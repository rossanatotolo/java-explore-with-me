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

        final Comment comment = new Comment();

        comment.setText(commentInputDto.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setEvent(event);

        return comment;
    }

    public static CommentOutputDto toCommentOutputDto(final Comment comment) {

        final CommentOutputDto commentOutputDto = new CommentOutputDto();

        commentOutputDto.setId(comment.getId());
        commentOutputDto.setText(comment.getText());
        commentOutputDto.setAuthorName(comment.getAuthor().getName());
        commentOutputDto.setEvent(EventMapper.toEventShortDto(comment.getEvent()));
        commentOutputDto.setCreated(comment.getCreated());

        return commentOutputDto;
    }

    public static List<CommentOutputDto> toCommentDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentOutputDto)
                .collect(Collectors.toList());
    }
}