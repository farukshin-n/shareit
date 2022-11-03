package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @Validated @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Get request for adding itemRequest {} from user with id={}", itemRequestDto, userId);
        return requestClient.postRequest(userId, itemRequestDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting request for all itemRequests from user with id={}", userId);
        return requestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                           int from,
                                                           @Positive @RequestParam(name = "size", defaultValue = "10")
                                                           int size) {
        log.info("Getting requests for all itemRequests from user with id={} with from {} and size {}",
                userId, from, size);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long requestId) {
        log.info("Getting request for itemRequest with id={} from user with id={}", requestId, userId);
        return requestClient.getRequest(userId, requestId);
    }
}
