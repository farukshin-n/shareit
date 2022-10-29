package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    void handleSearchItems() {
        User firstUser = new User(1L, "Adam", "adar@paradise.com");
        userRepository.save(firstUser);
        Item item = itemRepository.save(new Item(1L, "apple", "great fruit", true, firstUser, null));

        List<Item> result = itemRepository.searchItems("fru", Pageable.unpaged());

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.isAvailable(), result.get(0).isAvailable());
    }

}