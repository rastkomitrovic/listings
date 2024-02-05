package com.listings.listings.mapper.impl;

import com.listings.listings.data.domain.ContactInfo;
import com.listings.listings.data.domain.Listing;
import com.listings.listings.kafka.domain.ListingEvent;
import com.listings.listings.kafka.domain.ListingEventContactInfo;
import com.listings.listings.kafka.domain.ListingEventMode;
import com.listings.listings.mapper.ListingMapper;
import com.listings.listings.rest.dto.listing.ContactInfoDto;
import com.listings.listings.rest.dto.listing.ListingDto;
import com.listings.listings.rest.dto.listing.ListingEventDto;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class DefaultListingMapper implements ListingMapper {
    @Override
    public ListingDto mapToListingDto(@NonNull Listing listing) {
        ContactInfoDto contactInfoDto = listing.getContactInfo() != null
                ? ContactInfoDto
                .builder()
                .firstName(listing.getContactInfo().getFirstName())
                .lastName(listing.getContactInfo().getLastName())
                .email(listing.getContactInfo().getEmail())
                .phoneNumber(listing.getContactInfo().getPhoneNumber())
                .build()
                : null;

        return ListingDto
                .builder()
                .id(listing.getId())
                .make(listing.getMake())
                .model(listing.getModel())
                .productionYear(listing.getProductionYear())
                .mileage(listing.getMileage())
                .transmissionType(listing.getTransmissionType())
                .fuelType(listing.getFuelType())
                .contactInfo(contactInfoDto)
                .dateCreated(listing.getDateCreated())
                .dateUpdated(listing.getDateUpdated())
                .build();
    }

    @Override
    public Listing mapToListing(@NonNull ListingEvent listingEvent) {
        ContactInfo contactInfo = listingEvent.getContactInfo() != null
                ? ContactInfo
                .builder()
                .firstName(listingEvent.getContactInfo().getFirstName())
                .lastName(listingEvent.getContactInfo().getLastName())
                .email(listingEvent.getContactInfo().getEmail())
                .phoneNumber(listingEvent.getContactInfo().getPhoneNumber())
                .build()
                : null;

        return Listing
                .builder()
                .id(listingEvent.getId())
                .make(listingEvent.getMake())
                .model(listingEvent.getModel())
                .productionYear(listingEvent.getProductionYear())
                .mileage(listingEvent.getMileage())
                .transmissionType(listingEvent.getTransmissionType())
                .fuelType(listingEvent.getFuelType())
                .contactInfo(contactInfo)
                .build();
    }

    @Override
    public ListingEvent mapToListingEvent(@NonNull ListingEventDto listingEventDto, @NonNull ListingEventMode mode) {

        ListingEventContactInfo contactInfo = listingEventDto.getContactInfo() != null
                ? ListingEventContactInfo
                .builder()
                .firstName(listingEventDto.getContactInfo().getFirstName())
                .lastName(listingEventDto.getContactInfo().getLastName())
                .email(listingEventDto.getContactInfo().getEmail())
                .phoneNumber(listingEventDto.getContactInfo().getPhoneNumber())
                .build()
                : null;

        return ListingEvent
                .builder()
                .id(listingEventDto.getId())
                .make(listingEventDto.getMake())
                .model(listingEventDto.getModel())
                .productionYear(listingEventDto.getProductionYear())
                .mileage(listingEventDto.getMileage())
                .transmissionType(listingEventDto.getTransmissionType())
                .fuelType(listingEventDto.getFuelType())
                .contactInfo(contactInfo)
                .mode(mode)
                .build();
    }
}
