package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotAvailableException;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    private final Set<Long> itemIds = new HashSet<>(Arrays.asList(6L, 7L));
    private final User firstUser = new User(1L, "Adam", "adam@paradise.com");
    private final UserDto firstUserDto = new UserDto(firstUser.getId(), firstUser.getName(), firstUser.getEmail());
    private final User secondUser = new User(2L, "Eva", "eva@paradise.com");
    private final UserDto secondUserDto = new UserDto(secondUser.getId(), secondUser.getName(), secondUser.getEmail());
    private final ItemRequest paradiseRequest = new ItemRequest(
            4L,
            "great garden",
            secondUser,
            LocalDateTime.of(2022, 10, 24, 12, 30, 0)
    );
    private final Item paradise = new Item(
            3L,
            "Paradise",
            "great garden without people",
            true,
            secondUser,
            paradiseRequest);
    private final ItemDto paradiseDto = new ItemDto(
            paradise.getId(),
            paradise.getName(),
            paradise.getDescription(),
            paradise.isAvailable(),
            secondUserDto,
            paradise.getRequest().getId() == null ? null : paradise.getRequest().getId()
    );
    private final Booking lastBooking = new Booking(
            6L,
            LocalDateTime.of(2022, 10, 20, 12, 30),
            LocalDateTime.of(2022, 10, 21, 13, 35),
            paradise,
            firstUser,
            BookingStatus.APPROVED
    );
    private final Booking nextBooking = new Booking(
            7L,
            LocalDateTime.of(2022, 10, 23, 12, 35),
            LocalDateTime.of(2022, 10, 24, 13, 0),
            paradise,
            firstUser,
            BookingStatus.APPROVED
    );
    private final ItemDtoWithBookingsAndComments paradiseDtoWithBookingsAndComments = new ItemDtoWithBookingsAndComments(
            paradise.getId(),
            paradise.getName(),
            paradise.getDescription(),
            paradise.isAvailable(),
            secondUserDto,
            paradise.getRequest().getId() == null ? null : paradise.getRequest().getId(),
            new ArrayList<>(),
            BookingMapper.toBookingDtoWithBookerID(lastBooking),
            BookingMapper.toBookingDtoWithBookerID(nextBooking)
    );
    private final Comment comment = new Comment(
            8L,
            "awesome",
            paradise,
            firstUser,
            LocalDateTime.of(2022, 10, 25, 18, 1)
    );
    private final CommentDto commentDto = new CommentDto(
            comment.getId(),
            comment.getText(),
            comment.getItem().getId(),
            comment.getAuthor().getId(),
            comment.getAuthor().getName(),
            comment.getCreated()
    );
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void handleCreateItem_byDefault() {
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(mockItemRequestRepository.findById(paradiseDto.getRequestId()))
                .thenReturn(Optional.of(paradiseRequest));
        Mockito
                .when(mockItemRepository.save(any(Item.class)))
                .thenReturn(paradise);
        ItemDto actual = itemService.createItem(firstUser.getId(), paradiseDto);

        assertEquals(paradiseDto, actual);
    }

    @Test
    void handleCreateItem_UserNotExist() {
        Mockito
                .when(mockUserRepository.findById(51L))
                .thenReturn(Optional.empty());

        assertThrows(SubstanceNotFoundException.class,
                () -> itemService.createItem(51L, paradiseDto));
    }

    @Test
    void handleCreateItem_RequestNotExist() {
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(mockItemRequestRepository.findById(paradiseDto.getRequestId()))
                .thenReturn(Optional.empty());

        assertThrows(SubstanceNotFoundException.class,
                () -> itemService.createItem(firstUser.getId(), paradiseDto));
    }

    @Test
    void handleGetItem_byDefault() {
        Mockito
                .when(mockItemRepository.findById(paradise.getId()))
                .thenReturn(Optional.of(paradise));
        ItemDto actual = itemService.getItem(paradise.getId());

        assertEquals(paradiseDto, actual);
    }

    @Test
    void handleGetItem_ItemNotExist() {
        Mockito
                .when(mockItemRepository.findById(51L))
                .thenReturn(Optional.empty());

        assertThrows(SubstanceNotFoundException.class,
                () -> itemService.getItem(51L));
    }

    @Test
    void handleGetItemDtoWithBookingsAndComments_byDefault() {
        Mockito
                .when(mockItemRepository.findById(paradise.getId()))
                .thenReturn(Optional.of(paradise));
        Mockito
                .when(mockUserRepository.existsById(secondUser.getId()))
                .thenReturn(true);
        Mockito
                .when(mockBookingRepository.getPastOrCurrentBookingByItemId(paradise.getId()))
                .thenReturn(Optional.of(lastBooking));
        Mockito
                .when(mockBookingRepository.getFutureBookingByItemId(paradise.getId()))
                .thenReturn(Optional.of(nextBooking));

        ItemDtoWithBookingsAndComments actual = itemService.getItemDtoWithBookingsAndComments(
                secondUser.getId(), paradise.getId());

        assertEquals(paradiseDtoWithBookingsAndComments, actual);
    }

    @Test
    void handleGetItemDtoWithBookingsAndComments_ItemNotExist() {
        lenient()
                .when(mockItemRepository.findById(54L))
                .thenReturn(Optional.empty());

        assertThrows(
                SubstanceNotFoundException.class,
                () -> itemService.getItemDtoWithBookingsAndComments(secondUser.getId(), 54L)
        );
    }

    @Test
    void handleGetItemDtoWithBookingsAndComments_UserIsOwner() {
        ItemDtoWithBookingsAndComments expected = new ItemDtoWithBookingsAndComments(
                paradise.getId(),
                paradise.getName(),
                paradise.getDescription(),
                paradise.isAvailable(),
                secondUserDto,
                paradise.getRequest().getId(),
                new ArrayList<>(),
                new BookingDtoWithBookerId(
                        6L,
                        LocalDateTime.of(2022, 10, 20, 12, 30),
                        LocalDateTime.of(2022, 10, 21, 13, 35),
                        paradiseDto,
                        firstUser.getId(),
                        BookingStatus.APPROVED
                ),new BookingDtoWithBookerId(
                        7L,
                        LocalDateTime.of(2022, 10, 23, 12, 35),
                        LocalDateTime.of(2022, 10, 24, 13, 0),
                        paradiseDto,
                        firstUser.getId(),
                        BookingStatus.APPROVED)
        );
        lenient()
                .when(mockUserRepository.existsById(secondUserDto.getId()))
                .thenReturn(true);
        Mockito
                .when(mockItemRepository.findById(paradise.getId())).thenReturn(Optional.of(paradise));
        Mockito
                .when(mockBookingRepository.getPastOrCurrentBookingByItemId(paradiseDto.getId()))
                .thenReturn(Optional.of(lastBooking));
        Mockito
                .when(mockBookingRepository.getFutureBookingByItemId(paradiseDto.getId()))
                .thenReturn(Optional.of(nextBooking));
        Mockito
                .when(mockCommentRepository.findCommentsByItem_Id(paradiseDto.getId()))
                .thenReturn(Collections.emptyList());

        ItemDtoWithBookingsAndComments actual = itemService.getItemDtoWithBookingsAndComments(
                secondUser.getId(),
                paradiseDto.getId()
        );

        assertEquals(expected, actual);
    }

    @Test
    void handleGetItemDtoWithBookingsAndComments_UserIsNotOwner() {
        ItemDtoWithBookingsAndComments expected = new ItemDtoWithBookingsAndComments(
                paradise.getId(),
                paradise.getName(),
                paradise.getDescription(),
                paradise.isAvailable(),
                secondUserDto,
                paradise.getRequest().getId(),
                new ArrayList<>(),
                null,
                null
        );
        lenient()
                .when(mockUserRepository.existsById(firstUserDto.getId()))
                .thenReturn(true);
        Mockito
                .when(mockItemRepository.findById(paradise.getId()))
                .thenReturn(Optional.of(paradise));
        Mockito
                .when(mockCommentRepository.findCommentsByItem_Id(paradise.getId()))
                .thenReturn(Collections.emptyList());
        ItemDtoWithBookingsAndComments actual = itemService.getItemDtoWithBookingsAndComments(
                firstUser.getId(),
                paradise.getId()
        );

        assertEquals(expected, actual);
    }

    @Test
    void handleGetAllItemsByUserId_byDefault() {
        Mockito
                .when(mockItemRepository.findByOwnerIdOrderById(secondUser.getId(), PageRequest.of(0, 1)))
                .thenReturn(List.of(paradise));
        lenient()
                .when(mockBookingRepository.getBookingsByItem_IdInOrderByEndAsc(itemIds))
                .thenReturn(List.of(lastBooking, nextBooking));
        ItemDtoWithBookingsAndComments expected = new ItemDtoWithBookingsAndComments(
                paradise.getId(),
                paradise.getName(),
                paradise.getDescription(),
                paradise.isAvailable(),
                secondUserDto,
                paradise.getRequest().getId(),
                new ArrayList<>(),
                null,
                null
        );
        List<ItemDtoWithBookingsAndComments> actual = itemService.getAllItemsByUserId(secondUser.getId(), 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void handleUpdateItem_byDefault() {
        Item figLeaf = new Item(5L,
                "figLeaf",
                "leaf of fig tree.",
                true,
                firstUser,
                null);
        ItemDto update = new ItemDto(figLeaf.getId(),
                "palma leaf",
                "great leaf of palma.",
                false,
                firstUserDto,
                null);
        ItemDto expected = new ItemDto(
                figLeaf.getId(),
                update.getName(),
                update.getDescription(),
                update.getAvailable(),
                update.getOwner(),
                null
        );
        Mockito
                .when(mockItemRepository.findById(figLeaf.getId()))
                .thenReturn(Optional.of(figLeaf));
        Mockito
                .when(mockItemRepository.save(figLeaf))
                .thenReturn(figLeaf);
        ItemDto actual = itemService.updateItem(firstUser.getId(), figLeaf.getId(), update);

        assertEquals(expected, actual);
    }

    @Test
    void handleUpdateItem_withNulls() {
        Item figLeaf = new Item(5L,
                "figLeaf",
                "leaf of fig tree.",
                true,
                firstUser,
                null);
        ItemDto update = new ItemDto(figLeaf.getId(),
                null,
                null,
                null,
                firstUserDto,
                null);
        ItemDto expected = new ItemDto(
                figLeaf.getId(),
                figLeaf.getName(),
                figLeaf.getDescription(),
                figLeaf.isAvailable(),
                update.getOwner(),
                null
        );
        Mockito
                .when(mockItemRepository.findById(figLeaf.getId()))
                .thenReturn(Optional.of(figLeaf));
        Mockito
                .when(mockItemRepository.save(figLeaf))
                .thenReturn(figLeaf);
        ItemDto actual = itemService.updateItem(firstUser.getId(), figLeaf.getId(), update);

        assertEquals(expected, actual);
    }

    @Test
    void handleUpdateItem_UserIsNotOwner() {
        Mockito
                .when(mockItemRepository.findById(paradise.getId()))
                .thenReturn(Optional.of(paradise));

        assertThrows(
                ForbiddenException.class,
                () -> itemService.updateItem(
                        firstUser.getId(),
                        paradise.getId(),
                        new ItemDto(secondUser.getId(), null, null, null, null, null)
                )
        );
    }

    @Test
    void handleUpdateItem_ItemNotExist() {
        Mockito
                .when(mockItemRepository.findById(53L))
                .thenReturn(Optional.empty());

        assertThrows(
                SubstanceNotFoundException.class,
                () -> itemService.updateItem(
                        secondUser.getId(),
                        53L,
                        new ItemDto(secondUser.getId(), null, null, null, null, null)
                )
        );
    }

    @Test
    void handleDeleteItem_byDefault() {
        Item apple = new Item(5L, "Apple", "very tasty fruit", true, firstUser, null);
        Mockito
                .when(mockItemRepository.findById(apple.getId()))
                .thenReturn(Optional.of(apple));
        itemService.deleteItem(firstUser.getId(), apple.getId());

        Mockito
                .verify(mockItemRepository, Mockito.times(1))
                .deleteById(apple.getId());
    }

    @Test
    void handleDeleteItem_ItemNotExist() {
        Mockito
                .when(mockItemRepository.findById(53L))
                .thenReturn(Optional.empty());

        assertThrows(SubstanceNotFoundException.class,
                () -> itemService.deleteItem(secondUser.getId(), 53L));
    }

    @Test
    void handleDeleteItem_UserIsNotOwner() {
        Mockito
                .when(mockItemRepository.findById(paradise.getId()))
                .thenReturn(Optional.of(paradise));

        assertThrows(ForbiddenException.class,
                () -> itemService.deleteItem(firstUser.getId(), paradise.getId()));
    }

    @Test
    void handleSearchItems_byDefault() {
        Mockito
                .when(mockItemRepository.searchItems("table", PageRequest.of(0, 1)))
                .thenReturn(List.of(paradise));
        List<ItemDto> actual = itemService.searchItems("table", 0, 1);

        assertEquals(List.of(paradiseDto), actual);
    }

    @Test
    void handleSearchItems_TextIsBlank() {
        List<ItemDto> actual = itemService.searchItems("", 0, 1);

        assertEquals(Collections.emptyList(), actual);
    }

    @Test
    void handleAddComment_byDefault() {
        Mockito
                .when(mockItemRepository.findById(paradise.getId()))
                .thenReturn(Optional.of(paradise));
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(mockBookingRepository.findBookingsByItem_IdAndBooker_IdAndEndIsBefore(
                                eq(paradise.getId()),
                                eq(firstUser.getId()),
                                any(LocalDateTime.class)
                        )
                )
                .thenReturn(List.of(lastBooking));
        Mockito.when(mockCommentRepository.save(any())).thenReturn(comment);
        CommentDto realComment = itemService.addComment(firstUser.getId(), paradise.getId(), commentDto);

        assertEquals(commentDto, realComment);
    }

    @Test
    void handleAddComment_ItemNotExist() {
        Mockito
                .when(mockItemRepository.findById(59L))
                .thenReturn(Optional.empty());

        assertThrows(
                SubstanceNotFoundException.class,
                () -> itemService.addComment(
                        firstUser.getId(),
                        59L,
                        commentDto
                )
        );
    }

    @Test
    void handleAddComment_UserNotExist() {
        Mockito
                .when(mockItemRepository.findById(paradise.getId()))
                .thenReturn(Optional.of(paradise));
        Mockito
                .when(mockUserRepository.findById(59L))
                .thenReturn(Optional.empty());

        assertThrows(
                SubstanceNotFoundException.class,
                () -> itemService.addComment(
                        59L,
                        paradise.getId(),
                        commentDto
                )
        );
    }

    @Test
    void handleAddComment_UserIsNotBooker() {
        Mockito
                .when(mockItemRepository.findById(paradise.getId()))
                .thenReturn(Optional.of(paradise));
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(mockBookingRepository.findBookingsByItem_IdAndBooker_IdAndEndIsBefore(
                                eq(paradise.getId()),
                                eq(firstUser.getId()),
                                any(LocalDateTime.class)
                        )
                )
                .thenReturn(Collections.emptyList());

        assertThrows(
                NotAvailableException.class,
                () -> itemService.addComment(firstUser.getId(), paradise.getId(), commentDto)
        );
    }
}
