package ru.practicum.shareit.item.repository;

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
        @Sql(value = {"/test/resources/item/item-repository-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"/test/resources/item/item-repository-test-after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
public class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final User firstUser = new User(1L, "Adam", "adam@paradise.com");
    private final User secondUser = new User(2L, "Eva", "eva@paradise.com");
    private final ItemRequest paradiseRequest = new ItemRequest(
            4L,
            "great garden",
            secondUser,
            LocalDateTime.of(2022, 10, 24, 12, 30, 0)
    );
    private final Item paradise = new Item(3L,
            "Paradise",
            "great garden without people",
            true,
            firstUser,
            paradiseRequest);

    @Test
    void handleFindByOwnerIdOrderById() {
        List<Item> result = itemRepository.findByOwnerIdOrderById(firstUser.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(paradise));
    }

    @Test
    void findByRequestIdOrderById() {
        List<Item> result = itemRepository.findByRequestIdOrderById(paradiseRequest.getId());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(paradise));
    }

    @Test
    void searchItem() {
        List<Item> result = itemRepository.searchItems("paradise", Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(paradise));
    }
}
