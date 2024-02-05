package com.listings.listings.mapper.impl;

import com.listings.listings.data.domain.ContactInfo;
import com.listings.listings.data.domain.FuelType;
import com.listings.listings.data.domain.Listing;
import com.listings.listings.data.domain.TransmissionType;
import com.listings.listings.kafka.domain.ListingEvent;
import com.listings.listings.kafka.domain.ListingEventContactInfo;
import com.listings.listings.kafka.domain.ListingEventMode;
import com.listings.listings.rest.dto.listing.ContactInfoDto;
import com.listings.listings.rest.dto.listing.ListingDto;
import com.listings.listings.rest.dto.listing.ListingEventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DefaultListingMapperTest {

    @InjectMocks
    private DefaultListingMapper defaultListingMapper;


    @Test
    public void testMapToListingDtoNoContactInfo() {
        Listing listing = Listing
                .builder()
                .id("id")
                .make("make")
                .model("model")
                .productionYear(2000)
                .mileage(20000L)
                .transmissionType(TransmissionType.AUTOMATIC)
                .fuelType(FuelType.DIESEL)
                .dateCreated(LocalDate.now())
                .dateUpdated(LocalDate.now().minusDays(1))
                .build();

        ListingDto listingDto = defaultListingMapper.mapToListingDto(listing);

        assertNotNull(listingDto);
        assertNotNull(listingDto.getId());
        assertNotNull(listingDto.getMake());
        assertNotNull(listingDto.getModel());
        assertNotNull(listingDto.getProductionYear());
        assertNotNull(listingDto.getMileage());
        assertNotNull(listingDto.getTransmissionType());
        assertNotNull(listingDto.getFuelType());
        assertNull(listingDto.getContactInfo());
        assertNotNull(listingDto.getDateCreated());
        assertNotNull(listingDto.getDateUpdated());

        assertEquals(listing.getId(), listingDto.getId());
        assertEquals(listing.getMake(), listingDto.getMake());
        assertEquals(listing.getModel(), listingDto.getModel());
        assertEquals(listing.getProductionYear(), listingDto.getProductionYear());
        assertEquals(listing.getMileage(), listingDto.getMileage());
        assertEquals(listing.getTransmissionType(), listingDto.getTransmissionType());
        assertEquals(listing.getFuelType(), listingDto.getFuelType());
        assertEquals(listing.getDateCreated(), listingDto.getDateCreated());
        assertEquals(listing.getDateUpdated(), listingDto.getDateUpdated());
    }

    @Test
    public void testMapToListingDto() {

        Listing listing = Listing
                .builder()
                .id("id1")
                .make("make1")
                .model("model1")
                .productionYear(20001)
                .mileage(200001L)
                .transmissionType(TransmissionType.MANUAL)
                .fuelType(FuelType.PETROL)
                .contactInfo(
                        ContactInfo
                                .builder()
                                .firstName("firstName")
                                .lastName("lastName")
                                .email("email")
                                .phoneNumber("phoneNumber")
                                .build()
                )
                .dateCreated(LocalDate.now())
                .dateUpdated(LocalDate.now().minusDays(1))
                .build();

        ListingDto listingDto = defaultListingMapper.mapToListingDto(listing);

        assertNotNull(listingDto);
        assertNotNull(listingDto.getId());
        assertNotNull(listingDto.getMake());
        assertNotNull(listingDto.getModel());
        assertNotNull(listingDto.getProductionYear());
        assertNotNull(listingDto.getMileage());
        assertNotNull(listingDto.getTransmissionType());
        assertNotNull(listingDto.getFuelType());
        assertNotNull(listingDto.getContactInfo());
        assertNotNull(listingDto.getDateCreated());
        assertNotNull(listingDto.getDateUpdated());

        assertEquals(listing.getId(), listingDto.getId());
        assertEquals(listing.getMake(), listingDto.getMake());
        assertEquals(listing.getModel(), listingDto.getModel());
        assertEquals(listing.getProductionYear(), listingDto.getProductionYear());
        assertEquals(listing.getMileage(), listingDto.getMileage());
        assertEquals(listing.getTransmissionType(), listingDto.getTransmissionType());
        assertEquals(listing.getFuelType(), listingDto.getFuelType());
        assertEquals(listing.getContactInfo().getFirstName(), listingDto.getContactInfo().getFirstName());
        assertEquals(listing.getContactInfo().getLastName(), listingDto.getContactInfo().getLastName());
        assertEquals(listing.getContactInfo().getEmail(), listingDto.getContactInfo().getEmail());
        assertEquals(listing.getContactInfo().getPhoneNumber(), listingDto.getContactInfo().getPhoneNumber());
        assertEquals(listing.getDateCreated(), listingDto.getDateCreated());
        assertEquals(listing.getDateUpdated(), listingDto.getDateUpdated());
    }

    @Test
    public void testMapToListingFromListingEventNoContactInfo() {
        ListingEvent listingEvent = ListingEvent
                .builder()
                .id("id")
                .make("make")
                .model("model")
                .productionYear(2000)
                .mileage(20000L)
                .transmissionType(TransmissionType.AUTOMATIC)
                .fuelType(FuelType.DIESEL)
                .build();

        Listing listing = defaultListingMapper.mapToListing(listingEvent);

        assertNotNull(listing);
        assertNotNull(listing.getId());
        assertNotNull(listing.getMake());
        assertNotNull(listing.getModel());
        assertNotNull(listing.getProductionYear());
        assertNotNull(listing.getMileage());
        assertNotNull(listing.getTransmissionType());
        assertNotNull(listing.getFuelType());
        assertNull(listing.getContactInfo());
        assertNull(listing.getDateCreated());
        assertNull(listing.getDateUpdated());

        assertEquals(listing.getId(), listingEvent.getId());
        assertEquals(listing.getMake(), listingEvent.getMake());
        assertEquals(listing.getModel(), listingEvent.getModel());
        assertEquals(listing.getProductionYear(), listingEvent.getProductionYear());
        assertEquals(listing.getMileage(), listingEvent.getMileage());
        assertEquals(listing.getTransmissionType(), listingEvent.getTransmissionType());
        assertEquals(listing.getFuelType(), listingEvent.getFuelType());
    }

    @Test
    public void testMapToListingFromListingEvent() {

        ListingEvent listingEvent = ListingEvent
                .builder()
                .id("id1")
                .make("make1")
                .model("model1")
                .productionYear(20001)
                .mileage(200001L)
                .transmissionType(TransmissionType.MANUAL)
                .fuelType(FuelType.PETROL)
                .contactInfo(
                        ListingEventContactInfo
                                .builder()
                                .firstName("firstName")
                                .lastName("lastName")
                                .email("email")
                                .phoneNumber("phoneNumber")
                                .build()
                )
                .build();

        Listing listing = defaultListingMapper.mapToListing(listingEvent);

        assertNotNull(listing);
        assertNotNull(listing.getId());
        assertNotNull(listing.getMake());
        assertNotNull(listing.getModel());
        assertNotNull(listing.getProductionYear());
        assertNotNull(listing.getMileage());
        assertNotNull(listing.getTransmissionType());
        assertNotNull(listing.getFuelType());
        assertNotNull(listing.getContactInfo());
        assertNull(listing.getDateCreated());
        assertNull(listing.getDateUpdated());

        assertEquals(listingEvent.getId(), listing.getId());
        assertEquals(listingEvent.getMake(), listing.getMake());
        assertEquals(listingEvent.getModel(), listing.getModel());
        assertEquals(listingEvent.getProductionYear(), listing.getProductionYear());
        assertEquals(listingEvent.getMileage(), listing.getMileage());
        assertEquals(listingEvent.getTransmissionType(), listing.getTransmissionType());
        assertEquals(listingEvent.getFuelType(), listing.getFuelType());
        assertEquals(listingEvent.getContactInfo().getFirstName(), listing.getContactInfo().getFirstName());
        assertEquals(listingEvent.getContactInfo().getLastName(), listing.getContactInfo().getLastName());
        assertEquals(listingEvent.getContactInfo().getEmail(), listing.getContactInfo().getEmail());
        assertEquals(listingEvent.getContactInfo().getPhoneNumber(), listing.getContactInfo().getPhoneNumber());
    }

    @Test
    public void testMapToListingEventNoContactInfo() {
        ListingEventDto listingEventDto = ListingEventDto
                .builder()
                .id("id")
                .make("make")
                .model("model")
                .productionYear(2000)
                .mileage(20000L)
                .transmissionType(TransmissionType.AUTOMATIC)
                .fuelType(FuelType.DIESEL)
                .build();
        ListingEventMode mode = ListingEventMode.CREATE;

        ListingEvent listingEvent = defaultListingMapper.mapToListingEvent(listingEventDto, mode);

        assertNotNull(listingEvent);
        assertNotNull(listingEvent.getId());
        assertNotNull(listingEvent.getMake());
        assertNotNull(listingEvent.getModel());
        assertNotNull(listingEvent.getProductionYear());
        assertNotNull(listingEvent.getMileage());
        assertNotNull(listingEvent.getTransmissionType());
        assertNotNull(listingEvent.getFuelType());
        assertNull(listingEvent.getContactInfo());
        assertNotNull(listingEvent.getMode());

        assertEquals(listingEventDto.getId(), listingEvent.getId());
        assertEquals(listingEventDto.getMake(), listingEvent.getMake());
        assertEquals(listingEventDto.getModel(), listingEvent.getModel());
        assertEquals(listingEventDto.getProductionYear(), listingEvent.getProductionYear());
        assertEquals(listingEventDto.getMileage(), listingEvent.getMileage());
        assertEquals(listingEventDto.getTransmissionType(), listingEvent.getTransmissionType());
        assertEquals(listingEventDto.getFuelType(), listingEvent.getFuelType());
        assertEquals(mode, listingEvent.getMode());
    }

    @Test
    public void testMapToListingEvent() {
        ListingEventDto listingEventDto = ListingEventDto
                .builder()
                .id("id")
                .make("make")
                .model("model")
                .productionYear(2000)
                .mileage(20000L)
                .transmissionType(TransmissionType.AUTOMATIC)
                .fuelType(FuelType.DIESEL)
                .contactInfo(
                        ContactInfoDto
                                .builder()
                                .firstName("firstName")
                                .lastName("lastName")
                                .email("email")
                                .phoneNumber("phoneNumber")
                                .build()
                )
                .build();
        ListingEventMode mode = ListingEventMode.CREATE;

        ListingEvent listingEvent = defaultListingMapper.mapToListingEvent(listingEventDto, mode);

        assertNotNull(listingEvent);
        assertNotNull(listingEvent.getId());
        assertNotNull(listingEvent.getMake());
        assertNotNull(listingEvent.getModel());
        assertNotNull(listingEvent.getProductionYear());
        assertNotNull(listingEvent.getMileage());
        assertNotNull(listingEvent.getTransmissionType());
        assertNotNull(listingEvent.getFuelType());
        assertNotNull(listingEvent.getContactInfo());
        assertNotNull(listingEvent.getMode());

        assertEquals(listingEventDto.getId(), listingEvent.getId());
        assertEquals(listingEventDto.getMake(), listingEvent.getMake());
        assertEquals(listingEventDto.getModel(), listingEvent.getModel());
        assertEquals(listingEventDto.getProductionYear(), listingEvent.getProductionYear());
        assertEquals(listingEventDto.getMileage(), listingEvent.getMileage());
        assertEquals(listingEventDto.getTransmissionType(), listingEvent.getTransmissionType());
        assertEquals(listingEventDto.getFuelType(), listingEvent.getFuelType());
        assertEquals(listingEventDto.getContactInfo().getFirstName(), listingEvent.getContactInfo().getFirstName());
        assertEquals(listingEventDto.getContactInfo().getLastName(), listingEvent.getContactInfo().getLastName());
        assertEquals(listingEventDto.getContactInfo().getEmail(), listingEvent.getContactInfo().getEmail());
        assertEquals(listingEventDto.getContactInfo().getPhoneNumber(), listingEvent.getContactInfo().getPhoneNumber());
        assertEquals(mode, listingEvent.getMode());
    }
}
