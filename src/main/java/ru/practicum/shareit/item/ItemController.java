package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("We have request for adding item {} by user with id {}.", itemDto, userId);
        return itemService.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("We have request for adding comment by user with id {} for item with id {}.",
                userId, itemId);
        return itemService.addComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingsAndComments getItem(@RequestHeader("X-Sharer-User-Id") Long id,
            @PathVariable Long itemId) {
        log.info("We have request for getting item with id {}.", itemId);
        return itemService.getItemDtoWithBookingsAndComments(id, itemId);
    }

    @GetMapping
    public List<ItemDtoWithBookingsAndComments> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("We have request for getting all items by user with id {}.", userId);
        return itemService.getAllItemsByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId,
            @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        log.info("We have request for updating item with id {} by user with id {}.", itemId, ownerId);
        return itemService.updateItem(ownerId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId
    ) {
        log.info("We have request for deleting item {} by user with id {}.", itemId, ownerId);
        itemService.deleteItem(ownerId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("We have request for search {}.", text);
        return itemService.searchItems(text);
    }
}
