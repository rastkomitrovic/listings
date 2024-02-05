package com.listings.listings.service;

import com.listings.listings.data.domain.Listing;
import com.listings.listings.data.repositories.ListingRepository;
import com.listings.listings.kafka.domain.ListingEvent;
import com.listings.listings.kafka.domain.ListingEventMode;
import com.listings.listings.kafka.producer.ListingsTopicProducer;
import com.listings.listings.mapper.ListingMapper;
import com.listings.listings.rest.dto.listing.*;
import com.listings.listings.util.ElasticSearchConstants;
import com.listings.listings.util.ListingsUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultListingServiceTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private ListingMapper listingMapper;

    @Mock
    private ListingsTopicProducer listingsTopicProducer;

    @InjectMocks
    private DefaultListingService defaultListingService;

    @Captor
    private ArgumentCaptor<CriteriaQuery> criteriaQueryArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<Listing>> listingsArgumentCaptor;

    @Test
    public void testGetAll() {
        Integer page = 0;
        Integer size = 10;
        ListingField sort = ListingField.MODEL;
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Long totalElements = 100L;
        List<Listing> listings = List.of(Listing.builder().id("1").build());
        PageRequest pageRequest = ListingsUtils.createPageRequest(page, size, sort.getValue(), sortDirection);
        PageImpl<Listing> pageObject = new PageImpl<>(listings, pageRequest, totalElements);
        when(listingRepository.findAll(pageRequest)).thenReturn(pageObject);

        List<ListingDto> listingDtos = List.of(ListingDto.builder().id("1").build());
        when(listingMapper.mapToListingsDtos(listings)).thenReturn(listingDtos);

        ListingPage listingPage = defaultListingService.getAll(page, size, sort, sortDirection);

        assertNotNull(listingPage);
        assertNotNull(listingPage.getPage());
        assertNotNull(listingPage.getSize());
        assertNotNull(listingPage.getSort());
        assertNotNull(listingPage.getSortDirection());
        assertNotNull(listingPage.getTotalElements());
        assertNotNull(listingPage.getTotalPages());
        assertNotNull(listingPage.getContent());
        assertFalse(listingPage.getContent().isEmpty());
        assertEquals(listingDtos.size(), listingPage.getContent().size());

        assertEquals(page, listingPage.getPage());
        assertEquals(size, listingPage.getSize());
        assertEquals(sort, listingPage.getSort());
        assertEquals(sortDirection, listingPage.getSortDirection());
        assertEquals(totalElements, listingPage.getTotalElements());
        assertEquals(pageObject.getTotalPages(), listingPage.getTotalPages());

        for (int i = 0; i < listingDtos.size(); i++) {
            assertEquals(listingDtos.get(i).getId(), listingPage.getContent().get(i).getId());
        }

        verify(listingRepository, times(1)).findAll(pageRequest);
        verify(listingMapper, times(1)).mapToListingsDtos(listings);

    }

    @Test
    public void testSearchAll() {

        Integer page = 0;
        Integer size = 10;
        ListingField sort = ListingField.MODEL;
        Sort.Direction sortDirection = Sort.Direction.ASC;
        SearchListingDto searchListingDto = SearchListingDto
                .builder()
                .searchListingItems(List.of(
                        SearchListingItemDto
                                .builder()
                                .field(ListingField.MAKE)
                                .value("make1")
                                .build(),
                        SearchListingItemDto
                                .builder()
                                .field(ListingField.MODEL)
                                .value("model1")
                                .build(),
                        SearchListingItemDto
                                .builder()
                                .field(ListingField.PRODUCTION_YEAR)
                                .value("2000")
                                .build()
                ))
                .build();

        List<Listing> listings = List.of(Listing.builder().id("1").build());
        List<ListingDto> listingDtos = List.of(ListingDto.builder().id("1").build());

        SearchHits<Listing> searchHits = new SearchHitsImpl<>(
                listings.size(),
                TotalHitsRelation.OFF,
                0f,
                null,
                null,
                listings
                        .stream()
                        .map(listing -> new SearchHit<>(null, null, null, 0f, null, null, null, null, null, null, listing))
                        .collect(Collectors.toList()),
                null,
                null
        );
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(Listing.class), eq(IndexCoordinates.of(ElasticSearchConstants.LISTINGS_INDEX_NAME))))
                .thenReturn(searchHits);

        when(listingMapper.mapToListingsDtos(listings)).thenReturn(listingDtos);

        ListingPage listingPage = defaultListingService.searchAll(searchListingDto, page, size, sort, sortDirection);

        assertNotNull(listingPage);
        assertNotNull(listingPage.getPage());
        assertNotNull(listingPage.getSize());
        assertNotNull(listingPage.getSort());
        assertNotNull(listingPage.getSortDirection());
        assertNotNull(listingPage.getTotalElements());
        assertNotNull(listingPage.getTotalPages());
        assertNotNull(listingPage.getContent());
        assertFalse(listingPage.getContent().isEmpty());
        assertEquals(listingDtos.size(), listingPage.getContent().size());

        assertEquals(page, listingPage.getPage());
        assertEquals(size, listingPage.getSize());
        assertEquals(sort, listingPage.getSort());
        assertEquals(sortDirection, listingPage.getSortDirection());
        assertEquals(listings.size(), listingPage.getTotalElements());
        assertEquals(1, listingPage.getTotalPages());

        for (int i = 0; i < listingDtos.size(); i++) {
            assertEquals(listingDtos.get(i).getId(), listingPage.getContent().get(i).getId());
        }

        verify(elasticsearchOperations, times(1)).search(criteriaQueryArgumentCaptor.capture(), eq(Listing.class), eq(IndexCoordinates.of(ElasticSearchConstants.LISTINGS_INDEX_NAME)));
        verify(listingMapper, times(1)).mapToListingsDtos(listings);

        CriteriaQuery criteriaQuery = criteriaQueryArgumentCaptor.getValue();

        assertNotNull(criteriaQuery);
        assertNotNull(criteriaQuery.getCriteria());
        assertNotNull(criteriaQuery.getCriteria().getCriteriaChain());
        assertFalse(criteriaQuery.getCriteria().getCriteriaChain().isEmpty());

        List<Criteria> criteriaList = criteriaQuery.getCriteria().getCriteriaChain();

        assertEquals(3, criteriaList.size());

        assertNotNull(criteriaList.get(0));
        assertNotNull(criteriaList.get(0).getField());
        assertNotNull(criteriaList.get(0).getField().getName());
        assertNotNull(criteriaList.get(0).getQueryCriteriaEntries());
        assertFalse(criteriaList.get(0).getQueryCriteriaEntries().isEmpty());
        assertEquals(1, criteriaList.get(0).getQueryCriteriaEntries().size());
        assertEquals(Criteria.OperationKey.MATCHES, criteriaList.get(0).getQueryCriteriaEntries().stream().findFirst().get().getKey());
        assertEquals("make1", criteriaList.get(0).getQueryCriteriaEntries().stream().findFirst().get().getValue());
        assertEquals("make", criteriaList.get(0).getField().getName());

        assertNotNull(criteriaList.get(1));
        assertNotNull(criteriaList.get(1).getField());
        assertNotNull(criteriaList.get(1).getField().getName());
        assertNotNull(criteriaList.get(1).getQueryCriteriaEntries());
        assertFalse(criteriaList.get(1).getQueryCriteriaEntries().isEmpty());
        assertEquals(1, criteriaList.get(1).getQueryCriteriaEntries().size());
        assertEquals(Criteria.OperationKey.MATCHES, criteriaList.get(1).getQueryCriteriaEntries().stream().findFirst().get().getKey());
        assertEquals("model1", criteriaList.get(1).getQueryCriteriaEntries().stream().findFirst().get().getValue());
        assertEquals("model", criteriaList.get(1).getField().getName());

        assertNotNull(criteriaList.get(2));
        assertNotNull(criteriaList.get(2).getField());
        assertNotNull(criteriaList.get(2).getField().getName());
        assertNotNull(criteriaList.get(2).getQueryCriteriaEntries());
        assertFalse(criteriaList.get(2).getQueryCriteriaEntries().isEmpty());
        assertEquals(1, criteriaList.get(2).getQueryCriteriaEntries().size());
        assertEquals(Criteria.OperationKey.MATCHES, criteriaList.get(2).getQueryCriteriaEntries().stream().findFirst().get().getKey());
        assertEquals("2000", criteriaList.get(2).getQueryCriteriaEntries().stream().findFirst().get().getValue());
        assertEquals("productionYear", criteriaList.get(2).getField().getName());
    }

    @Test
    public void testProduceListingEvent() {
        ListingEventDto listingEventDto = ListingEventDto.builder().id("12").build();
        ListingEventMode mode = ListingEventMode.CREATE;
        ListingEvent listingEvent = ListingEvent.builder().id("12").build();

        when(listingMapper.mapToListingEvent(listingEventDto, mode)).thenReturn(listingEvent);
        doNothing().when(listingsTopicProducer).produceListing(listingEvent);

        defaultListingService.produceListingEvent(listingEventDto, mode);

        verify(listingMapper, times(1)).mapToListingEvent(listingEventDto, mode);
        verify(listingsTopicProducer, times(1)).produceListing(listingEvent);
    }

    @Test
    public void testGenerateRandomListings() {

        when(listingRepository.saveAll(anyList())).thenReturn(List.of());

        defaultListingService.generateRandomListings();

        verify(listingRepository, times(1)).saveAll(listingsArgumentCaptor.capture());

        List<Listing> listings = listingsArgumentCaptor.getValue();

        assertNotNull(listings);
        assertFalse(listings.isEmpty());
        assertEquals(40, listings.size());
        listings
                .forEach(listing -> {
                    assertNull(listing.getId());
                    assertNotNull(listing.getMake());
                    assertNotNull(listing.getModel());
                    assertNotNull(listing.getProductionYear());
                    assertNotNull(listing.getMileage());
                    assertNotNull(listing.getTransmissionType());
                    assertNotNull(listing.getFuelType());
                    assertNotNull(listing.getContactInfo());
                    assertNotNull(listing.getContactInfo().getFirstName());
                    assertNotNull(listing.getContactInfo().getLastName());
                    assertNotNull(listing.getContactInfo().getEmail());
                    assertNotNull(listing.getContactInfo().getPhoneNumber());
                    assertNotNull(listing.getDateCreated());
                    assertNull(listing.getDateUpdated());
                });
    }
}
