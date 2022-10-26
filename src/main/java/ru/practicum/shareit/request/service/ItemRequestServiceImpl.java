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
        log.info(String.format("New itemrequest with id %d created successfully.", newItemRequest.getId()));

        return  ItemRequestMapper.itemRequestToDto(newItemRequest);
    }

    @Override
    public List<ItemRequestDtoWithItems> getRequests(long userId) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't user with id %d in database.", userId)));

        return itemRequestRepository.findByRequesterIdOrderByCreatedDesc(user.getId())
                .stream()
                .map(itemRequest -> ItemRequestMapper.itemRequestToDtoWithItems(
                        itemRequest, getItemsByRequestId(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoWithItems> getAllRequests(long userId, int from, int size) {
        return itemRequestRepository
                .findByRequesterIdNot(userId,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(itemRequest -> ItemRequestMapper.itemRequestToDtoWithItems(
                        itemRequest, getItemsByRequestId(itemRequest.getId())))
                .collect(Collectors.toList());
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

    private List<ItemDtoForRequests> getItemsByRequestId(long requestId) {
        return ItemMapper.toItemDtoForRequestsList(
                new ArrayList<>(itemRepository.findByRequestIdOrderById(requestId))
        );
    }
}
