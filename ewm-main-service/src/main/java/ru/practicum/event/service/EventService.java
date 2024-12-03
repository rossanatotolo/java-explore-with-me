package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.controller.EventControllerPublic;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

public interface EventService {
    List<EventShortDto> getAllEvents(final Long userId, final int from, final int size);

    EventFullDto createEvent(final Long userId, final EventNewDto eventRequestDto);

    EventFullDto getEventById(final Long userId, final Long eventId);

    EventFullDto updateEvent(final Long userId, final Long eventId, final EventUpdateDto eventUpdateDto);

    List<ParticipationRequestDto> getRequestsByEventId(final Long userId, final Long eventId);

    Map<String, List<ParticipationRequestDto>> approveRequests(final Long userId, final Long eventId,
                                                               final EventRequestStatusUpdateRequest requestUpdateDto);

    List<EventFullDto> getAllByAdmin(final List<Long> users, final List<String> states, final List<Long> categories,
                                     final String rangeStart, final String rangeEnd, final int from, final int size);

    EventFullDto approveEventByAdmin(final Long eventId, final EventUpdateDto eventUpdateDto);

    List<EventShortDto> getAllPublic(final String text, final List<Long> categories, final Boolean paid,
                                     final String rangeStart, final String rangeEnd, final boolean onlyAvailable,
                                     final EventControllerPublic.EventSort sort, final int from, final int size,
                                     final HttpServletRequest request);

    EventFullDto getEventByIdPublic(final Long eventId, final HttpServletRequest request);
}

