package ru.practicum.shareit.item.repository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.SubstanceNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Component
@Slf4j
public class InMemoryItemRepository implements ItemRepository {
    private long id = 1L;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Long>> ownerWithItems = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);

        List<Long> listOfItemIdsByUser;
        if (ownerWithItems.containsKey(item.getOwnerId())) {
            listOfItemIdsByUser = ownerWithItems.get(item.getOwnerId());
        } else {
            listOfItemIdsByUser = new ArrayList<>();
        }
        listOfItemIdsByUser.add(item.getId());
        ownerWithItems.put(item.getOwnerId(), listOfItemIdsByUser);

        log.info("Item {} successfully added.", item);
        return item;
    }

    @Override
    public Item getItem(long itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new SubstanceNotFoundException(String.format("Item with id %d doesn't exist in database.", itemId));
        }
    }

    @Override
    public List<Item> getAllItemsOfUser(long userId) {
        if (ownerWithItems.containsKey(userId)) {
            List<Item> resultListOfItems = new ArrayList<>();
            for (long id : ownerWithItems.get(userId)) {
                resultListOfItems.add(getItem(id));
            }
            log.info("List of all items of user with id {} returned.", userId);
            return resultListOfItems;
        } else {
            throw new SubstanceNotFoundException(String.format("User with id: %d haven't any item yet.", userId));
        }
    }

    @Override
    public Item updateItem(Item item) {
        if (items.containsKey(item.getId())) {
            items.replace(item.getId(), item);
        } else {
            throw new SubstanceNotFoundException(
                    String.format("There isn't item with id %d in database.", item.getId()));
        }
        log.info("Item with id {} owned by user with id {} successfully updated.",
                item.getId(),
                item.getOwnerId());
        return item;
    }

    @Override
    public void deleteItem(long itemId) {
        ownerWithItems.get(items.get(itemId).getOwnerId()).remove(itemId);
        items.remove(itemId);
        log.info("Item with id {} successfully removed.", itemId);
    }

    @Override
    public List<Item> searchItems(String text) {
        String textForSearch = text.trim().toLowerCase();
        return textForSearch.isEmpty() ? new ArrayList<>() :
                items.values().stream().filter(Item::isAvailable).filter(i ->
                        i.getName().toLowerCase().contains(textForSearch) ||
                        i.getDescription().toLowerCase().contains(textForSearch)).collect(Collectors.toList());
    }

    private long generateId() {
        return id++;
    }
}
