package com.listings.listings.service;


import com.listings.listings.kafka.domain.ListingEventMode;
import com.listings.listings.rest.dto.listing.ListingEventDto;
import com.listings.listings.rest.dto.listing.ListingField;
import com.listings.listings.rest.dto.listing.ListingPage;
import com.listings.listings.rest.dto.listing.SearchListingDto;
import lombok.NonNull;
import org.springframework.data.domain.Sort;

public interface ListingService {

    /**
     * Retrieves all the listings with pagination.
     *
     * @param page          - Page number.
     * @param size          - Page size.
     * @param sort          - Sort param.
     * @param sortDirection - Sort direction.
     * @return - {@link ListingPage} object representing the listings page.
     */
    ListingPage getAll(@NonNull Integer page, @NonNull Integer size, @NonNull ListingField sort, @NonNull Sort.Direction sortDirection);

    /**
     * Searches all the listings by the desired criteria.
     *
     * @param searchListingsDto - Object representing the search criteria.
     * @param page              - Page number.
     * @param size              - Page size.
     * @param sort              - Sort param.
     * @param sortDirection     - Sort direction.
     * @return - {@link ListingPage} object representing the listings page by the desired search criteria.
     */
    ListingPage searchAll(@NonNull SearchListingDto searchListingsDto, @NonNull Integer page, @NonNull Integer size, @NonNull ListingField sort, @NonNull Sort.Direction sortDirection);

    /**
     * Produces a listing event to the listings topic.
     *
     * @param listingEventDto - {@link ListingEventDto} object.
     * @param mode            - {@link ListingEventMode} object representing the mode.
     */
    void produceListingEvent(@NonNull ListingEventDto listingEventDto, @NonNull ListingEventMode mode);

    /**
     * Generates random listings and saves them to the database.
     */
    void generateRandomListings();
}
