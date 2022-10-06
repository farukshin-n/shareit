package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item);
    Item getItem(long itemId);
    List<Item> getAllItemsOfUser(long userId);
    Item updateItem(Item item);
    void deleteItem(long itemId);
    List<Item> searchItems(String text);
}
