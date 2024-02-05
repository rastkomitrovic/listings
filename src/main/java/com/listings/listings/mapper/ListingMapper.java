package com.listings.listings.mapper;

import com.listings.listings.data.domain.Listing;
import com.listings.listings.kafka.domain.ListingEvent;
import com.listings.listings.kafka.domain.ListingEventMode;
import com.listings.listings.rest.dto.listing.ListingDto;
import com.listings.listings.rest.dto.listing.ListingEventDto;
import lombok.NonNull;

import java.util.List;

public interface ListingMapper {

    /**
     * Maps the object of {@link Listing} to {@link ListingDto} object.
     *
     * @param listing - {@link Listing} object.
     * @return - {@link ListingDto} object.
     */
    ListingDto mapToListingDto(@NonNull Listing listing);

    /**
     * Maps the list of {@link Listing} objects to list of {@link ListingDto} obejects.
     *
     * @param listings - List of {@link Listing} objects.
     * @return - List of {@link ListingDto} objects.
     */
    default List<ListingDto> mapToListingsDtos(@NonNull List<Listing> listings) {
        return listings
                .stream()
                .map(this::mapToListingDto)
                .toList();
    }

    /**
     * Maps the {@link ListingEvent} object to {@link Listing} object.
     *
     * @param listingEvent - {@link ListingEvent} object.
     * @return - {@link Listing} object.
     */
    Listing mapToListing(@NonNull ListingEvent listingEvent);

    /**
     * Maps the {@link ListingEventDto} object to {@link ListingEvent} object.
     *
     * @param listingEventDto - {@link ListingEventDto} object.
     * @param mode            - {@link ListingEventMode} object indicating what operation is to be executed.
     * @return - {@link ListingEvent} object.
     */
    ListingEvent mapToListingEvent(@NonNull ListingEventDto listingEventDto, @NonNull ListingEventMode mode);
}
