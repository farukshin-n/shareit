package ru.practicum.shareit.request.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(value = {"request-repository-test.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"request-repository-test.after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
public class ItemRequestRepositoryTest {
    private final ItemRequestRepository requestRepository;
    private final User firstUser = new User(1L, "Adam", "adam@paradise.com");
    private final User secondUser = new User(2L, "Eva", "eva@paradise.com");
    private final ItemRequest request = new ItemRequest(
            4L,
            "great garden without people",
            secondUser,
            LocalDateTime.of(2022, 10, 15, 15, 15));
    private final Item paradise = new Item(3L,
            "Paradise",
            "great garden without people",
            true,
            firstUser,
            request);
    private final ItemRequest secondRequest = new ItemRequest(
            6L,
            "very tasty fruit",
            firstUser,
            LocalDateTime.of(2022, 10, 20, 12, 12));
    private final Item apple = new Item(5L, "Apple", "very tasty fruit", true, secondUser, secondRequest);

    @Test
    void handleFindByRequesterIdOrderByCreatedDesc() {
        List<ItemRequest> result = requestRepository.findByRequesterIdOrderByCreatedDesc(firstUser.getId());

        assertThat(result).isNotEmpty();
        assertEquals(result.get(0).getId(), secondRequest.getId());
        assertEquals(result.get(0).getDescription(), secondRequest.getDescription());
        assertEquals(result.get(0).getRequester().getId(), secondRequest.getRequester().getId());
        assertEquals(result.get(0).getCreated(), secondRequest.getCreated());
    }

    @Test
    void handleFindByRequesterIdNot() {
        List<ItemRequest> result = requestRepository.findByRequesterIdNot(firstUser.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty();
        assertEquals(result.get(0).getId(), request.getId());
        assertEquals(result.get(0).getDescription(), request.getDescription());
        assertEquals(result.get(0).getRequester().getId(), request.getRequester().getId());
        assertEquals(result.get(0).getCreated(), request.getCreated());
    }
}
