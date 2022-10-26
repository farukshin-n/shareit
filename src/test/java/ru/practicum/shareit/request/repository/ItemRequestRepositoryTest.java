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
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(value = {"itemrequest-repository-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"itemrequest-repository-test.after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
public class ItemRequestRepositoryTest {
    private final ItemRequestRepository requestRepository;
    private final User firstUser = new User(1L, "Adam", "adam@paradise.com");
    private final User secondUser = new User(2L, "Eva", "eva@paradise.com");
    private final ItemRequest paradiseRequest = new ItemRequest(
            4L,
            "nice garden without people",
            secondUser,
            LocalDateTime.of(2022, 10, 10, 12, 0, 0)
    );
    private final Item paradise = new Item(
            3L,
            "Paradise",
            "great garden without people",
            true,
            firstUser,
            paradiseRequest);
    private final ItemRequest appleRequest = new ItemRequest(
            6L,
            "very tasty fruit snake recommend",
            firstUser,
            LocalDateTime.of(2022, 10, 12, 13, 00, 0));
    private final Item apple = new Item(
            5L,
            "Apple",
            "very tasty fruit snake recommend",
            true,
            secondUser,
            appleRequest
    );

    @Test
    void handleFindByRequesterIdOrderByCreatedDesc_byDefault() {
        List<ItemRequest> result = requestRepository.findByRequesterIdOrderByCreatedDesc(firstUser.getId());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(appleRequest));
    }

    @Test
    void handleFindByRequesterIdNot_byDefault() {
        List<ItemRequest> result = requestRepository.findByRequesterIdNot(firstUser.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(paradiseRequest));
    }
}
