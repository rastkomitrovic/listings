package com.listings.listings.rest;

import com.listings.listings.kafka.domain.ListingEventMode;
import com.listings.listings.rest.dto.listing.*;
import com.listings.listings.service.ListingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListingRestControllerTest {

    @Mock
    private ListingService listingService;

    @InjectMocks
    private ListingRestController listingRestController;

    @Test
    public void testGetAll() {
        Integer page = 0;
        Integer size = 10;
        ListingField sort = ListingField.ID;
        Sort.Direction sortDirection = Sort.Direction.ASC;

        ListingPage listingPage = ListingPage
                .builder()
                .page(page)
                .size(size)
                .sort(sort)
                .sortDirection(sortDirection)
                .totalElements(3L)
                .totalPages(5)
                .content(
                        List.of(
                                ListingDto
                                        .builder()
                                        .id("1")
                                        .build(),
                                ListingDto
                                        .builder()
                                        .id("2")
                                        .build(),
                                ListingDto
                                        .builder()
                                        .id("3")
                                        .build()
                        )
                )
                .build();

        when(listingService.getAll(page, size, sort, sortDirection)).thenReturn(listingPage);

        ResponseEntity<ListingPage> responseEntity = listingRestController.getAll(page, size, sort, sortDirection);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        assertNotNull(responseEntity.getBody().getPage());
        assertNotNull(responseEntity.getBody().getSize());
        assertNotNull(responseEntity.getBody().getSort());
        assertNotNull(responseEntity.getBody().getSortDirection());
        assertNotNull(responseEntity.getBody().getTotalElements());
        assertNotNull(responseEntity.getBody().getTotalPages());
        assertNotNull(responseEntity.getBody().getContent());
        assertFalse(responseEntity.getBody().getContent().isEmpty());
        assertEquals(listingPage.getContent().size(), responseEntity.getBody().getContent().size());
        for (int i = 0; i < listingPage.getContent().size(); i++) {
            assertEquals(listingPage.getContent().get(i), responseEntity.getBody().getContent().get(i));
        }

        verify(listingService, times(1)).getAll(page, size, sort, sortDirection);
    }

    @Test
    public void testSearchAll() {
        Integer page = 0;
        Integer size = 10;
        ListingField sort = ListingField.ID;
        Sort.Direction sortDirection = Sort.Direction.ASC;
        SearchListingDto searchListingDto = SearchListingDto
                .builder()
                .searchListingItems(List.of(
                        SearchListingItemDto
                                .builder()
                                .field(ListingField.ID)
                                .value("value")
                                .build(),
                        SearchListingItemDto
                                .builder()
                                .field(ListingField.MAKE)
                                .value("value2")
                                .build()
                ))
                .build();

        ListingPage listingPage = ListingPage
                .builder()
                .page(page)
                .size(size)
                .sort(sort)
                .sortDirection(sortDirection)
                .totalElements(3L)
                .totalPages(5)
                .content(
                        List.of(
                                ListingDto
                                        .builder()
                                        .id("1")
                                        .build(),
                                ListingDto
                                        .builder()
                                        .id("2")
                                        .build(),
                                ListingDto
                                        .builder()
                                        .id("3")
                                        .build()
                        )
                )
                .build();

        when(listingService.searchAll(searchListingDto, page, size, sort, sortDirection)).thenReturn(listingPage);

        ResponseEntity<ListingPage> responseEntity = listingRestController.searchAll(searchListingDto, page, size, sort, sortDirection);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        assertNotNull(responseEntity.getBody().getPage());
        assertNotNull(responseEntity.getBody().getSize());
        assertNotNull(responseEntity.getBody().getSort());
        assertNotNull(responseEntity.getBody().getSortDirection());
        assertNotNull(responseEntity.getBody().getTotalElements());
        assertNotNull(responseEntity.getBody().getTotalPages());
        assertNotNull(responseEntity.getBody().getContent());
        assertFalse(responseEntity.getBody().getContent().isEmpty());
        assertEquals(listingPage.getContent().size(), responseEntity.getBody().getContent().size());

        for (int i = 0; i < listingPage.getContent().size(); i++) {
            assertEquals(listingPage.getContent().get(i), responseEntity.getBody().getContent().get(i));
        }

        verify(listingService, times(1)).searchAll(searchListingDto, page, size, sort, sortDirection);
    }

    @Test
    public void testProduceListingEvent() {
        ListingEventDto listingEventDto = ListingEventDto.builder().build();
        ListingEventMode mode = ListingEventMode.CREATE;

        doNothing().when(listingService).produceListingEvent(listingEventDto, mode);

        ResponseEntity<Void> responseEntity = listingRestController.produceListingEvent(listingEventDto, mode);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        verify(listingService, times(1)).produceListingEvent(listingEventDto, mode);
    }

    @Test
    public void testGenerateRandomListings() {

        doNothing().when(listingService).generateRandomListings();

        ResponseEntity<Void> responseEntity = listingRestController.generateRandomListings();

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
