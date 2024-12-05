package ru.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventControllerPrivate {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllEvents(@PathVariable @Positive final Long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                            @RequestParam(defaultValue = "10") @Positive final int size) {
        return eventService.getAllEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable @Positive final Long userId, @RequestBody @Valid final EventNewDto eventNewDto) {
        return eventService.createEvent(userId, eventNewDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable @Positive final Long userId, @PathVariable @Positive final Long eventId) {
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable @Positive final Long userId, @PathVariable @Positive final Long eventId,
                               @RequestBody @Valid final EventUpdateDto eventUpdateDto) {
        return eventService.updateEvent(userId, eventId, eventUpdateDto);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByEventId(@PathVariable final Long userId, @PathVariable final Long eventId) {
        return eventService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, List<ParticipationRequestDto>> approveRequests(@PathVariable final Long userId,
                                                                      @PathVariable final Long eventId,
                                                                      @RequestBody @Valid final EventRequestStatusUpdateRequest requestUpdateDto) {
        return eventService.approveRequests(userId, eventId, requestUpdateDto);
    }
}
