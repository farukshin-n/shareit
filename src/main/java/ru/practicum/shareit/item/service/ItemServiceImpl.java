package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.exception.CommentFromUserWithoutBookingException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.SubstanceNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserMapper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        final UserDto owner = UserMapper.toUserDto(
                userRepository.findById(userId)
                        .orElseThrow(() -> new SubstanceNotFoundException(
                                String.format("There isn't user with id %d in database.", userId)))
        );
        final Long itemRequestId = itemDto.getRequestId();
        ItemRequest request = null;
        if (itemRequestId != null) {
            request = requestRepository.findById(itemRequestId)
                    .orElseThrow(() -> new SubstanceNotFoundException(
                            String.format("There isn't request with id %d in database.", itemRequestId)
                    ));
        }
        final Item item = itemRepository.save(
                ItemMapper.toItem(itemDto, owner, request)
        );
        log.info("New item with id {} by user with id {} created successfully.",
                item.getId(),
                item.getOwner().getId());

        return ItemMapper.itemToDto(item);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't item with id %d in database.", itemId)
        ));

        return ItemMapper.itemToDto(item);
    }

    @Override
    public ItemDtoWithBookingsAndComments getItemDtoWithBookingsAndComments(Long userId, Long itemId) {
        if (!userRepository.existsById(userId)) {
            throw new SubstanceNotFoundException(
                    String.format("There isn't user with id %d in database.", userId)
            );
        }
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't item with id %d in database.", itemId)
        ));
        BookingDtoWithBookerId currentOrPastBooking = null;
        BookingDtoWithBookerId futureBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            currentOrPastBooking = bookingRepository.getPastOrCurrentBookingByItemId(item.getId())
                    .map(BookingMapper::toBookingDtoWithBookerID)
                    .orElse(null);
            futureBooking = bookingRepository.getFutureBookingByItemId(item.getId())
                    .map(BookingMapper::toBookingDtoWithBookerID)
                    .orElse(null);
        }
        List<CommentDto> comments = commentRepository.findCommentsByItem_Id(item.getId())
                .stream().map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        return ItemMapper.toItemDtoWithBookingsAndComments(
                item,
                comments,
                currentOrPastBooking,
                futureBooking
        );
    }

    @Override
    public List<ItemDtoWithBookingsAndComments> getAllItemsByUserId(Long userId) {
        List<ItemDtoWithBookingsAndComments> resultList = new ArrayList<>();
        List<Item> items = itemRepository.findByOwnerIdOrderById(userId);
        Set<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toSet());
        List<Comment> comments = commentRepository.findCommentsByItem_IdIn(itemIds);
        List<Booking> bookings = bookingRepository.getBookingsByItem_IdInOrderByEndAsc(itemIds);

        for (Item item : items) {
            List<CommentDto> commentDtos = new ArrayList<>();
            for (Comment comment : comments) {
                if (comment.getItem().equals(item)) {
                    commentDtos.add(CommentMapper.toCommentDto(comment));
                }
            }
            List<Booking> pastOrCurrentBooking = bookings.stream()
                            .filter(booking -> booking.getItem().equals(item))
                            .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                            .limit(1)
                            .collect(Collectors.toList());
            BookingDtoWithBookerId lastBooking = null;
            if (pastOrCurrentBooking.size() != 0) {
                lastBooking = BookingMapper.toBookingDtoWithBookerID(pastOrCurrentBooking.get(0));
            }
            List<Booking> futureBooking = bookings.stream()
                            .filter(booking -> booking.getItem().equals(item))
                            .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                            .limit(1)
                            .collect(Collectors.toList());
            BookingDtoWithBookerId nextBooking = null;
            if (futureBooking.size() != 0) {
                nextBooking = BookingMapper.toBookingDtoWithBookerID(futureBooking.get(0));
            }

            resultList.add(
                    ItemMapper.toItemDtoWithBookingsAndComments(
                            item,
                            commentDtos,
                            lastBooking,
                            nextBooking
            ));
        }
        return resultList;
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't item with id %d in database.", itemId)
        ));
        if (!ownerId.equals(item.getOwner().getId())) {
            throw new ForbiddenException(String.format(
                    "User with id %d cannot edit item with %d",
                    ownerId,
                    itemId)
            );
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(item);
        log.info("Item with id {} by user {} updated successfully.",
                updatedItem.getId(),
                updatedItem.getOwner().getId());
        return ItemMapper.itemToDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteItem(Long ownerId, Long itemId) {
        final ItemDto itemDto = getItem(itemId);
        if (ownerId.equals(itemDto.getOwner().getId())) {
            itemRepository.deleteById(itemId);
            log.info("Item with id {} by user {} deleted successfully.",
                    itemId,
                    ownerId);
        } else {
            throw new ForbiddenException(String.format(
                    "User with id %d cannot delete item with %d",
                    ownerId,
                    itemId)
            );
        }
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.searchItems(text)
                .stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto) {
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't item with id %d in database.", itemId)
        ));
        final User author = userRepository.findById(authorId).orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't user with id %d in database.", authorId)
        ));
        final List<Booking> bookings = bookingRepository
                .findBookingsByItem_IdAndBooker_IdAndEndIsBefore(itemId, authorId, LocalDateTime.now());
        if (bookings.stream().findAny().isEmpty()) {
            throw new CommentFromUserWithoutBookingException(
                    String.format("User with id %d hasn't any bookings and has no rights to add comments.", authorId)
            );
        }
        final Comment comment = CommentMapper.toComment(commentDto, item, author);
        commentRepository.save(comment);
        log.info(String.format("Comment from user with id %d added successfully.", authorId));

        return CommentMapper.toCommentDto(comment);
    }
}
