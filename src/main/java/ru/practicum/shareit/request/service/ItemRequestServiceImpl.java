package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.SubstanceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto addRequest(long userId, ItemRequestDto itemRequestDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new SubstanceNotFoundException(
                        String.format("There isn't user with id %d in database.", userId)));
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestDto, user);
        ItemRequest newItemRequest = itemRequestRepository.save(itemRequest);
        log.info("New itemrequest with id {} created successfully.", newItemRequest.getId());

        return  ItemRequestMapper.itemRequestToDto(newItemRequest);
    }

    @Override
    public List<ItemRequestDtoWithItems> getRequests(long userId) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't user with id %d in database.", userId)));
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(user.getId());

        return getRequestDtoWithItemsListByRequests(requests);
    }

    @Override
    public List<ItemRequestDtoWithItems> getAllRequests(long userId, int from, int size) {
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNot(
                userId,
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"))
        );

        return getRequestDtoWithItemsListByRequests(requests);
    }

    @Override
    public ItemRequestDtoWithItems getRequest(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't user with id %d in database.", userId)));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new SubstanceNotFoundException(String.format(
                        "There isn't request with id %d in database.", requestId)));
        List<Item> items = new ArrayList<>(itemRepository.findByRequestIdOrderById(itemRequest.getId()));

        return ItemRequestMapper.itemRequestToDtoWithItems(itemRequest,
                ItemMapper.toItemDtoForRequestsList(items));
    }

    private List<ItemDtoForRequests> getItemsByRequestId(long requestId, List<Item> items) {
        List<Item> itemsForMapping = items
                .stream()
                .filter(item -> item.getRequest().getId() == requestId)
                .collect(Collectors.toList());

        return ItemMapper.toItemDtoForRequestsList(itemsForMapping);
    }

    private List<ItemRequestDtoWithItems> getRequestDtoWithItemsListByRequests(List<ItemRequest> requests) {
        List<Long> requestIds = requests
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> itemsForMapping = itemRepository.getByRequestIdIn(requestIds);

        return requests.stream().map(itemRequest -> ItemRequestMapper.itemRequestToDtoWithItems(
                        itemRequest, getItemsByRequestId(itemRequest.getId(), itemsForMapping)))
                .collect(Collectors.toList());
    }
}
