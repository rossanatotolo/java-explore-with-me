package ru.practicum.event.mapper;

import ru.practicum.User.dto.UserDtoShort;
import ru.practicum.User.model.User;
import ru.practicum.category.dto.CategoryOutputDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventMapper {

    public static Event toEvent(final EventNewDto eventRequestDto,
                                final User initiator,
                                final Category category) {

        final Event event = new Event();

        event.setInitiator(initiator);
        event.setAnnotation(eventRequestDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(eventRequestDto.getDescription());
        event.setEventDate(eventRequestDto.getEventDate());
        event.setLocation(eventRequestDto.getLocation());
        event.setPaid(eventRequestDto.getPaid());
        event.setParticipantLimit(eventRequestDto.getParticipantLimit());
        event.setRequestModeration(eventRequestDto.getRequestModeration());
        event.setTitle(eventRequestDto.getTitle());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setConfirmedRequests(0);

        return event;
    }

    public static EventShortDto toEventShortDto(final Event event) {

        final EventShortDto eventShortDto = new EventShortDto();

        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(
                new CategoryOutputDto(event.getCategory().getId(), event.getCategory().getName()));
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setId(event.getId());
        eventShortDto.setInitiator(
                new UserDtoShort(event.getInitiator().getId(), event.getInitiator().getName()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setViews(0);

        return eventShortDto;
    }

    public static EventFullDto toEventFullDto(final Event event) {

        final EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(
                new CategoryOutputDto(event.getCategory().getId(), event.getCategory().getName()));
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(
                new UserDtoShort(event.getInitiator().getId(), event.getInitiator().getName()));
        eventFullDto.setLocation(event.getLocation());
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(event.getViews());

        return eventFullDto;
    }

    public static List<EventShortDto> toEventShortDtoList(Iterable<Event> events) {
        List<EventShortDto> result = new ArrayList<>();

        for (Event event : events) {
            result.add(toEventShortDto(event));
        }
        return result;
    }

    public static List<EventFullDto> toEventFullDtoList(Iterable<Event> events) {
        List<EventFullDto> result = new ArrayList<>();

        for (Event event : events) {
            result.add(toEventFullDto(event));
        }
        return result;
    }
}
