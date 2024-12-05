package ru.practicum.comment.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentInputDto;
import ru.practicum.comment.dto.CommentOutputDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class CommentControllerPrivate {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentOutputDto> getAllComments(@PathVariable @Positive final Long userId,
                                                 @PathVariable @Positive final Long eventId,
                                                 @RequestParam(defaultValue = "0") final int from,
                                                 @RequestParam(defaultValue = "10") final int size) {
        return commentService.getAllComments(userId, eventId, from, size);
    }

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentOutputDto createComment(@RequestBody @Validated final CommentInputDto commentInputDto,
                                          @PathVariable @Positive final Long userId,
                                          @PathVariable @Positive final Long eventId) {
        return commentService.createComment(commentInputDto, userId, eventId);
    }

    @PatchMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentOutputDto updateComment(@RequestBody @Validated final CommentInputDto commentInputDto,
                                          @PathVariable @Positive final Long userId,
                                          @PathVariable @Positive final Long commentId) {
        return commentService.updateComment(commentInputDto, userId, commentId);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive final Long userId, @PathVariable @Positive final Long commentId) {
        commentService.deleteComment(userId, commentId);
    }

    @GetMapping("/events/{eventId}/comments/search")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentOutputDto> searchComments(@PathVariable final Long userId, @PathVariable final Long eventId,
                                                 @RequestParam @NotBlank final String text,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero final Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive final Integer size) {
        return commentService.searchComments(userId, eventId, text, from, size);
    }
}
