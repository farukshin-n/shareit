package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long id,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(id, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoWithItems> getRequests(@RequestHeader("X-Sharer-User-Id") long id) {
        return itemRequestService.getRequests(id);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItems> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") long id,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return itemRequestService.getAllRequests(id, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItems getRequest(@RequestHeader("X-Sharer-User-Id") final long id,
                                              @PathVariable final long requestId) {
        return itemRequestService.getRequest(id, requestId);
    }
}
