package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.User.model.User;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByCategoryId(Long catId);

    List<Event> findByInitiatorId(final Long initiatorId, final Pageable pageable);

    List<Event> findByInitiatorInAndStateInAndCategoryInAndEventDateAfterAndEventDateBeforeOrderByIdAsc(
            final List<User> initiators, final List<EventState> states, final List<Category> categories,
            final LocalDateTime start, final LocalDateTime end, final Pageable pageable);

    Optional<Event> findByIdAndState(final Long eventId, final EventState state);

    List<Event> findByIdIn(final List<Long> eventIds);
}
