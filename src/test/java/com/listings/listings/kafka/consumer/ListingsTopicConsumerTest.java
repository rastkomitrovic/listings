package com.listings.listings.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.listings.listings.data.domain.ContactInfo;
import com.listings.listings.data.domain.FuelType;
import com.listings.listings.data.domain.Listing;
import com.listings.listings.data.domain.TransmissionType;
import com.listings.listings.data.repositories.ListingRepository;
import com.listings.listings.kafka.domain.ListingEvent;
import com.listings.listings.kafka.domain.ListingEventMode;
import com.listings.listings.mapper.ListingMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ListingsTopicConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private ListingMapper listingMapper;

    @InjectMocks
    private ListingsTopicConsumer listingsTopicConsumer;

    @Captor
    private ArgumentCaptor<Listing> listingArgumentCaptor;

    @Test
    public void testConsumeListingDelete() throws JsonProcessingException {
        String message = "{}";
        Acknowledgment acknowledgment = Mockito.mock(Acknowledgment.class);
        doNothing().when(acknowledgment).acknowledge();
        ListingEvent listingEvent = ListingEvent
                .builder()
                .mode(ListingEventMode.DELETE)
                .build();
        Listing listing = Listing
                .builder()
                .id("12")
                .build();

        when(objectMapper.readValue(message, ListingEvent.class)).thenReturn(listingEvent);
        when(listingMapper.mapToListing(listingEvent)).thenReturn(listing);
        when(listingRepository.existsById(listing.getId())).thenReturn(true);
        doNothing().when(listingRepository).deleteById(listing.getId());

        listingsTopicConsumer.consumeListing(message, acknowledgment);

        verify(objectMapper, times(1)).readValue(message, ListingEvent.class);
        verify(listingMapper, times(1)).mapToListing(listingEvent);
        verify(listingRepository, times(1)).existsById(listing.getId());
        verify(listingRepository, times(1)).deleteById(listing.getId());
    }

    @Test
    public void testConsumeListingDeleteNotExisting() throws JsonProcessingException {
        String message = "{}";
        Acknowledgment acknowledgment = Mockito.mock(Acknowledgment.class);
        doNothing().when(acknowledgment).acknowledge();
        ListingEvent listingEvent = ListingEvent
                .builder()
                .mode(ListingEventMode.DELETE)
                .build();
        Listing listing = Listing
                .builder()
                .id("12")
                .build();

        when(objectMapper.readValue(message, ListingEvent.class)).thenReturn(listingEvent);
        when(listingMapper.mapToListing(listingEvent)).thenReturn(listing);
        when(listingRepository.existsById(listing.getId())).thenReturn(false);

        listingsTopicConsumer.consumeListing(message, acknowledgment);

        verify(objectMapper, times(1)).readValue(message, ListingEvent.class);
        verify(listingMapper, times(1)).mapToListing(listingEvent);
        verify(listingRepository, times(1)).existsById(listing.getId());
        verify(listingRepository, times(0)).deleteById(listing.getId());
    }

    @Test
    public void testConsumeListingDeleteValidationFailed() throws JsonProcessingException {
        String message = "{}";
        Acknowledgment acknowledgment = Mockito.mock(Acknowledgment.class);
        doNothing().when(acknowledgment).acknowledge();
        ListingEvent listingEvent = ListingEvent
                .builder()
                .mode(ListingEventMode.DELETE)
                .build();
        Listing listing = Listing
                .builder()
                .build();

        when(objectMapper.readValue(message, ListingEvent.class)).thenReturn(listingEvent);
        when(listingMapper.mapToListing(listingEvent)).thenReturn(listing);

        listingsTopicConsumer.consumeListing(message, acknowledgment);

        verify(objectMapper, times(1)).readValue(message, ListingEvent.class);
        verify(listingMapper, times(1)).mapToListing(listingEvent);
        verify(listingRepository, times(0)).existsById(listing.getId());
        verify(listingRepository, times(0)).deleteById(listing.getId());
    }

    @Test
    public void testConsumeListingCreate() throws JsonProcessingException {
        String message = "{}";
        Acknowledgment acknowledgment = Mockito.mock(Acknowledgment.class);
        doNothing().when(acknowledgment).acknowledge();
        ListingEvent listingEvent = ListingEvent
                .builder()
                .mode(ListingEventMode.CREATE)
                .build();
        Listing listing = Listing
                .builder()
                .build();

        when(objectMapper.readValue(message, ListingEvent.class)).thenReturn(listingEvent);
        when(listingMapper.mapToListing(listingEvent)).thenReturn(listing);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.setMake("make");

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.setModel("model");

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.setProductionYear(1800);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.setProductionYear(LocalDate.now().getYear() + 1);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.setProductionYear(2000);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.setMileage(-10L);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.setMileage(2000L);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.setTransmissionType(TransmissionType.AUTOMATIC);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.setFuelType(FuelType.DIESEL);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.setContactInfo(ContactInfo.builder().build());

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.getContactInfo().setFirstName("firstName");

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.getContactInfo().setLastName("lastName");

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.getContactInfo().setEmail("email");

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        listing.getContactInfo().setPhoneNumber("phoneNumber");

        when(listingRepository.save(listing)).thenReturn(listing);

        listingsTopicConsumer.consumeListing(message, acknowledgment);

        verify(listingRepository, times(1)).save(listingArgumentCaptor.capture(), eq(RefreshPolicy.IMMEDIATE));

        Listing savedListing = listingArgumentCaptor.getValue();

        assertNotNull(savedListing);

        assertNotNull(savedListing.getDateCreated());

    }

    @Test
    public void testConsumeListingUpdate() throws JsonProcessingException {
        String message = "{}";
        Acknowledgment acknowledgment = Mockito.mock(Acknowledgment.class);
        doNothing().when(acknowledgment).acknowledge();
        ListingEvent listingEvent = ListingEvent
                .builder()
                .mode(ListingEventMode.UPDATE)
                .build();
        Listing listing = Listing
                .builder()
                .build();

        Listing oldListing = Listing
                .builder()
                .id("id")
                .make("make")
                .model("model")
                .productionYear(2000)
                .mileage(2000L)
                .transmissionType(TransmissionType.AUTOMATIC)
                .fuelType(FuelType.DIESEL)
                .contactInfo(ContactInfo
                        .builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .email("email")
                        .phoneNumber("phoneNumber")
                        .build())
                .dateCreated(LocalDate.now())
                .build();

        when(objectMapper.readValue(message, ListingEvent.class)).thenReturn(listingEvent);
        when(listingMapper.mapToListing(listingEvent)).thenReturn(listing);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.setId("id");
        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.setMake("make");

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.setModel("model");

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.setProductionYear(1800);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.setProductionYear(LocalDate.now().getYear() + 1);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.setProductionYear(2000);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.setMileage(-10L);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.setMileage(2000L);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.setTransmissionType(TransmissionType.AUTOMATIC);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.setFuelType(FuelType.DIESEL);

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.setContactInfo(ContactInfo.builder().build());

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.getContactInfo().setFirstName("firstName");

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.getContactInfo().setLastName("lastName");

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.getContactInfo().setEmail("email");

        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        listing.getContactInfo().setPhoneNumber("phoneNumber");

        when(listingRepository.findById(listing.getId())).thenReturn(Optional.empty());
        listingsTopicConsumer.consumeListing(message, acknowledgment);
        verify(listingRepository, times(0)).save(any(), any());

        when(listingRepository.findById(listing.getId())).thenReturn(Optional.of(oldListing));
        listingsTopicConsumer.consumeListing(message, acknowledgment);

        verify(listingRepository, times(1)).save(listingArgumentCaptor.capture(), any());

        Listing savedListing = listingArgumentCaptor.getValue();

        assertNotNull(savedListing);

        assertNotNull(savedListing.getDateUpdated());

    }
}
