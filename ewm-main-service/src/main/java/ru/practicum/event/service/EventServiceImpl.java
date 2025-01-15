package ru.practicum.event.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.ParticipationRequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.ParticipationRequestStatus;
import ru.practicum.request.repository.ParticipationRequestRepository;
import ru.practicum.stat.service.StatsService;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final EntityManager entityManager;
    private final StatsService statsService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllEvents(final Long userId, final int from, final int size) {
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size);

        final List<Event> events = eventRepository.findByInitiatorId(userId, pageable);

        if (events.isEmpty()) {
            log.info("Списка событий у пользователя с id = {} не найдено.", userId);
            return new ArrayList<>();
        }
        log.info("Получение списка событий пользователя с id = {}.", userId);
        return EventMapper.toEventShortDtoList(events);

    }

    @Override
    public EventFullDto createEvent(final Long userId, final EventNewDto eventRequestDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} не существует." + userId));
        Long catId = eventRequestDto.getCategory();
        final Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new ValidationException("Категории с id = {} не существует." + catId));

        Location location = eventRequestDto.getLocation();
        locationRepository.save(location);

        if (eventRequestDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Дата и время намеченного события не соответствует требованиям.");
        }

        final Event event = eventRepository.save(EventMapper.toEvent(eventRequestDto, user, category));
        log.info("Событие с id = {} и со статусом: {} добавлено", user.getId(), event.getState());
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(final Long userId, final Long eventId) {
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} не существует." + eventId));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ValidationException("Пользователь не является инициатором этого события.");
        }

        log.info("Получение события с id = {}", eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(final Long userId, final Long eventId, final EventUpdateDto eventUpdateDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} не существует." + userId));
        final Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} не существует." + eventId));

        if (!Objects.equals(oldEvent.getInitiator().getId(), user.getId())) {
            throw new ValidationException("Редактирование доступно только пользователю, иницировавшему событие.");
        }

        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Редактирование недоступно для опубликованного события, либо статус не существует.");
        }

        if (Objects.nonNull(eventUpdateDto.getEventDate()) &&
                eventUpdateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время намеченного события не соответствует требованиям.");
        }

        if (Objects.nonNull(eventUpdateDto.getCategory())) {
            Long catId = eventUpdateDto.getCategory();
            final Category category = categoryRepository.findById(eventUpdateDto.getCategory())
                    .orElseThrow(() -> new ValidationException("Категории с id = {} не существует." + catId));
            oldEvent.setCategory(category);
        }

        Optional.ofNullable(eventUpdateDto.getTitle()).ifPresent(oldEvent::setTitle);
        Optional.ofNullable(eventUpdateDto.getAnnotation()).ifPresent(oldEvent::setAnnotation);
        Optional.ofNullable(eventUpdateDto.getDescription()).ifPresent(oldEvent::setDescription);
        Optional.ofNullable(eventUpdateDto.getEventDate()).ifPresent(oldEvent::setEventDate);
        Optional.ofNullable(eventUpdateDto.getLocation()).ifPresent(oldEvent::setLocation);
        Optional.ofNullable(eventUpdateDto.getParticipantLimit()).ifPresent(oldEvent::setParticipantLimit);
        Optional.ofNullable(eventUpdateDto.getPaid()).ifPresent(oldEvent::setPaid);
        Optional.ofNullable(eventUpdateDto.getRequestModeration()).ifPresent(oldEvent::setRequestModeration);

        if (Objects.nonNull(eventUpdateDto.getStateAction())) {
            if (eventUpdateDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                oldEvent.setState(EventState.PENDING);
            } else if (eventUpdateDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                oldEvent.setState(EventState.CANCELED);
            }
        }

        final Event event = eventRepository.save(oldEvent);
        log.info("Событие с id = {} обновлено.", eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByEventId(final Long userId, final Long eventId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} не существует." + userId));
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} не существует." + eventId));

        if (!Objects.equals(event.getInitiator(), user)) {
            throw new ConflictException("Пользователь не является инициатором этого события.");
        }

        final List<ParticipationRequest> requests = participationRequestRepository.findByEventId(eventId);
        if (requests.isEmpty()) {
            log.info("Заявок на участие в мероприятии с id = {} нет.", eventId);
            return new ArrayList<>();
        }

        log.info("Получение списка заявок на участие в мероприятии с id {}.", eventId);
        return ParticipationRequestMapper.toParticipationRequestDtoList(requests);
    }

    @Override
    public Map<String, List<ParticipationRequestDto>> approveRequests(final Long userId, final Long eventId,
                                                                      final EventRequestStatusUpdateRequest requestUpdateDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} не существует." + userId));
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} не существует." + eventId));

        if (!Objects.equals(event.getInitiator(), user)) {
            throw new ConflictException("Пользователь не является инициатором этого события.");
        }

        final List<ParticipationRequest> requests = participationRequestRepository.findRequestByIdIn(requestUpdateDto.getRequestIds());

        if (event.getRequestModeration() && event.getParticipantLimit().equals(event.getConfirmedRequests()) &&
                event.getParticipantLimit() != 0 && requestUpdateDto.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
            throw new ConflictException("Лимит заявок на участие в событии исчерпан.");
        }

        final boolean verified = requests.stream()
                .allMatch(request -> request.getEvent().getId().longValue() == eventId);
        if (!verified) {
            throw new ConflictException("Список запросов не относятся к одному событию.");
        }

        final Map<String, List<ParticipationRequestDto>> requestMap = new HashMap<>();

        if (requestUpdateDto.getStatus().equals(ParticipationRequestStatus.REJECTED)) {
            if (requests.stream()
                    .anyMatch(request -> request.getStatus().equals(ParticipationRequestStatus.CONFIRMED))) {
                throw new ConflictException("Запрос на установление статуса <ОТМЕНЕНА>. Подтвержденые заявки нельзя отменить.");
            }
            log.info("Запрос на отклонение заявки подтвержден.");

            List<ParticipationRequestDto> rejectedRequests = requests.stream()
                    .peek(request -> request.setStatus(ParticipationRequestStatus.REJECTED))
                    .map(participationRequestRepository::save)
                    .map(ParticipationRequestMapper::toParticipationRequestDto)
                    .toList();
            requestMap.put("rejectedRequests", rejectedRequests);
        } else {
            if (requests.stream()
                    .anyMatch(request -> !request.getStatus().equals(ParticipationRequestStatus.PENDING))) {
                throw new ConflictException("Запрос на установление статуса <ПОДТВЕРЖДЕНА>. Заявки должны быть со статусом <В ОЖИДАНИИ>.");
            }

            long limit = event.getParticipantLimit() - event.getConfirmedRequests();
            final List<ParticipationRequest> confirmedList = requests.stream()
                    .limit(limit)
                    .peek(request -> request.setStatus(ParticipationRequestStatus.CONFIRMED))
                    .map(participationRequestRepository::save).toList();
            log.info("Заявки на участие сохранены со статусом <ПОДТВЕРЖДЕНА>.");

            final List<ParticipationRequestDto> confirmedRequests = confirmedList.stream()
                    .map(ParticipationRequestMapper::toParticipationRequestDto)
                    .toList();
            requestMap.put("confirmedRequests", confirmedRequests);

            final List<ParticipationRequest> rejectedList = requests.stream()
                    .skip(limit)
                    .peek(request -> request.setStatus(ParticipationRequestStatus.REJECTED))
                    .map(participationRequestRepository::save).toList();
            log.info("Часть заявок на участие сохранены со статусом <ОТМЕНЕНА>, в связи с превышением лимита.");
            final List<ParticipationRequestDto> rejectedRequests = rejectedList.stream()
                    .map(ParticipationRequestMapper::toParticipationRequestDto)
                    .toList();
            requestMap.put("rejectedRequests", rejectedRequests);

            event.setConfirmedRequests(confirmedList.size() + event.getConfirmedRequests());
            eventRepository.save(event);
        }
        return requestMap;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllByAdmin(final List<Long> usersId, final List<String> states, final List<Long> categoriesId,
                                            final String rangeStart, final String rangeEnd, final int from, final int size) {
        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, FORMATTER) : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, FORMATTER) : LocalDateTime.now().plusYears(20);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (start.isAfter(end)) {
            throw new ValidationException("Временной промежуток задан неверно.");
        }

        List<User> users;
        if (Objects.isNull(usersId) || usersId.isEmpty()) {
            users = userRepository.findAll();
            if (users.isEmpty()) {
                log.info("Еще нет ни одного пользователя, а значит и событий нет.");
                return new ArrayList<>();
            }
        } else {
            users = userRepository.findByIdInOrderByIdAsc(usersId, pageRequest);
            if (users.size() != usersId.size()) {
                throw new ValidationException("Список пользователей передан неверно.");
            }
        }

        final List<EventState> eventStates;
        if (Objects.isNull(states) || states.isEmpty()) {
            eventStates = List.of(EventState.PUBLISHED, EventState.CANCELED, EventState.PENDING);
        } else {
            try {
                eventStates = states.stream()
                        .map(EventState::valueOf)
                        .toList();
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Недопустимое значение статуса: " + e.getMessage());
            }
        }

        List<Category> categories;
        if (categoriesId == null) {
            categories = categoryRepository.findAll();
            if (categories.isEmpty()) {
                log.info("Еще нет ни одной категории, а значит и событий нет");
                return new ArrayList<>();
            }
        } else {
            categories = categoryRepository.findByIdInOrderByIdAsc(categoriesId, pageRequest);
            if (categories.size() != categoriesId.size()) {
                throw new ValidationException("Список категорий передан неверно неверно");
            }
        }

        final List<Event> events = eventRepository
                .findByInitiatorInAndStateInAndCategoryInAndEventDateAfterAndEventDateBeforeOrderByIdAsc(
                        users, eventStates, categories, start, end, pageRequest);

        if (events.isEmpty()) {
            log.info("По данным параметрам не нашлось ни одного события");
            return new ArrayList<>();
        }

        log.info("Получен список событий по заданным параметрам");
        return EventMapper.toEventFullDtoList(events);
    }

    @Override
    public EventFullDto approveEventByAdmin(final Long eventId, final EventUpdateDto eventUpdateDto) {
        final Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} не существует." + eventId));

        if ((Objects.nonNull(eventUpdateDto.getEventDate()) &&
                eventUpdateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) ||
                oldEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Событие не может начинаться ранее, чем через 1 час после редактирования.");
        }

        if (oldEvent.getPublishedOn() != null && LocalDateTime.now().plusHours(1).isBefore(oldEvent.getPublishedOn())) {
            throw new ConflictException("Дата и время изменяемого события не соответствует требованиям.");
        }

        if (oldEvent.getState().equals(EventState.PUBLISHED) ||
                oldEvent.getState().equals(EventState.CANCELED)) {
            throw new ConflictException("Редактирование статуса недоступно для опубликованного или отмененного события.");
        }

        if (Objects.nonNull(eventUpdateDto.getCategory())) {
            Long catId = eventUpdateDto.getCategory();
            final Category category = categoryRepository.findById(eventUpdateDto.getCategory())
                    .orElseThrow(() -> new ValidationException("Категории с id = {} не существует." + catId));
            oldEvent.setCategory(category);
        }

        Optional.ofNullable(eventUpdateDto.getTitle()).ifPresent(oldEvent::setTitle);
        Optional.ofNullable(eventUpdateDto.getAnnotation()).ifPresent(oldEvent::setAnnotation);
        Optional.ofNullable(eventUpdateDto.getDescription()).ifPresent(oldEvent::setDescription);
        Optional.ofNullable(eventUpdateDto.getEventDate()).ifPresent(oldEvent::setEventDate);
        Optional.ofNullable(eventUpdateDto.getLocation()).ifPresent(oldEvent::setLocation);
        Optional.ofNullable(eventUpdateDto.getParticipantLimit()).ifPresent(oldEvent::setParticipantLimit);
        Optional.ofNullable(eventUpdateDto.getPaid()).ifPresent(oldEvent::setPaid);
        Optional.ofNullable(eventUpdateDto.getRequestModeration()).ifPresent(oldEvent::setRequestModeration);

        if (Objects.nonNull(eventUpdateDto.getStateAction()) && oldEvent.getState().equals(EventState.PENDING) &&
                eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            oldEvent.setState(EventState.PUBLISHED);
            oldEvent.setPublishedOn(LocalDateTime.now());
        }

        if (Objects.nonNull(eventUpdateDto.getStateAction()) && oldEvent.getState().equals(EventState.PENDING) &&
                eventUpdateDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
            oldEvent.setState(EventState.CANCELED);
            oldEvent.setPublishedOn(null);
        }

        final Event event = eventRepository.save(oldEvent);
        log.info("Событие с id = {} обновлено администратором.", eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllPublic(final String text, final List<Long> categories, final Boolean paid,
                                            final String rangeStart, final String rangeEnd, final boolean onlyAvailable,
                                            final EventSort sort, final int from, final int size,
                                            final HttpServletRequest request) {
        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, FORMATTER) : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, FORMATTER) : LocalDateTime.now().plusYears(20);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (start.isAfter(end)) {
            throw new ValidationException("Временной промежуток задан неверно.");
        }

        StringBuilder queryStr = new StringBuilder("""
                SELECT e
                FROM Event e
                JOIN FETCH e.category c
                WHERE e.eventDate >= :start AND e.eventDate <= :end
                """);

        if (Objects.nonNull(text) && !text.isEmpty()) {
            queryStr.append(" AND (LOWER(e.annotation) LIKE LOWER(:text) OR LOWER(e.description) LIKE LOWER(:text))");
        }
        if (Objects.nonNull(categories) && !categories.isEmpty()) {
            queryStr.append(" AND e.category.id IN :categories");
        }
        if (Objects.nonNull(paid)) {
            queryStr.append(" AND e.paid = :paid");
        }

        queryStr.append(" AND e.participantLimit > e.confirmedRequests");
        TypedQuery<Event> query = entityManager.createQuery(queryStr.toString(), Event.class)
                .setParameter("start", start)
                .setParameter("end", end);

        if (Objects.nonNull(text) && !text.isEmpty()) {
            query.setParameter("text", "%" + text + "%");
        }
        if (Objects.nonNull(categories) && !categories.isEmpty()) {
            query.setParameter("categories", categories);
        }
        if (Objects.nonNull(paid)) {
            query.setParameter("paid", paid);
        }

        query.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize());
        query.setMaxResults(pageRequest.getPageSize());

        List<Event> events = query.getResultList();
        Map<Long, Long> eventAndViews = statsService.getView(events.stream().map(Event::getId).toList(), false);
        events.forEach(e -> e.setViews(Math.toIntExact(eventAndViews.getOrDefault(e.getId(), 0L))));

        if (Objects.nonNull(sort)) {
            if (sort.equals(EventSort.EVENT_DATE)) {
                events.sort(Comparator.comparing(Event::getEventDate));
            } else if (sort.equals(EventSort.VIEWS)) {
                events.sort(Comparator.comparing(Event::getViews).reversed());
            }
        }
        if (events.stream().noneMatch(e -> e.getState().equals(EventState.PUBLISHED))) {
            throw new ValidationException("Нет опубликованных событий.");
        }

        List<Event> paginatedEvents = events.stream().skip(from).toList();
        statsService.createStats(request.getRequestURI(), request.getRemoteAddr());

        return paginatedEvents.stream()
                .map(EventMapper::toEventShortDto)
                .peek(dto -> {
                    Long viewCount = eventAndViews.get(dto.getId());
                    dto.setViews(viewCount != null ? viewCount.intValue() : 0);
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdPublic(final Long eventId, final HttpServletRequest request) {
        final Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("События с id = {} не существует." + eventId));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("У события должен быть статус <ОПУБЛИКОВАННО>.");
        }
        final Map<Long, Long> view = statsService.getView(new ArrayList<>(List.of(event.getId())), true);
        final EventFullDto eventResponseLongDto = EventMapper.toEventFullDto(event);
        eventResponseLongDto.setViews(Math.toIntExact(view.getOrDefault(event.getId(), 0L)));

        statsService.createStats(request.getRequestURI(), request.getRemoteAddr());
        return eventResponseLongDto;
    }
}
