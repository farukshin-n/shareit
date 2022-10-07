package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Stream;

public interface ItemRepository {
    Item addItem(Item item);

    Item getItem(long itemId);

    List<Item> getAllItemsOfUser(long userId);

    Item updateItem(Item item);

    void deleteItem(long itemId);

    Stream<Item> searchItems(String text);
}
