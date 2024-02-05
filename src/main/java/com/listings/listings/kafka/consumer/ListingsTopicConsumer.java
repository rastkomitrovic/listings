package com.listings.listings.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.listings.listings.data.domain.Listing;
import com.listings.listings.data.repositories.ListingRepository;
import com.listings.listings.kafka.domain.ListingEvent;
import com.listings.listings.kafka.domain.ListingEventMode;
import com.listings.listings.mapper.ListingMapper;
import com.listings.listings.util.CacheConstants;
import com.listings.listings.util.KafkaConstants;
import com.listings.listings.util.ListingsException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListingsTopicConsumer {

    private final ObjectMapper objectMapper;

    private final ListingRepository listingRepository;

    private final ListingMapper listingMapper;

    /**
     * Consumes messages from the listings topic.
     *
     * @param message        - Serialized listing event.
     * @param acknowledgment - {@link Acknowledgment} object used to acknowledge the receiving of the object or not.
     */
    @KafkaListener(topics = {KafkaConstants.LISTINGS_TOPIC})
    @Transactional
    @CacheEvict(value = {CacheConstants.ALL_LISTINGS_CACHE, CacheConstants.SEARCH_LISTINGS_CACHE}, allEntries = true)
    public void consumeListing(String message, Acknowledgment acknowledgment) {
        if (message == null) {
            log.info("Got null message");
            acknowledgment.acknowledge();
        }
        ListingEvent listingEvent = null;
        try {
            listingEvent = objectMapper.readValue(message, ListingEvent.class);

            Listing listing = listingMapper.mapToListing(listingEvent);

            validateListing(listing, listingEvent.getMode());

            switch (listingEvent.getMode()) {
                case DELETE -> doDeleteListing(listing.getId());
                case CREATE -> doCreateListing(listing);
                case UPDATE -> doUpdateListing(listing);
            }

            acknowledgment.acknowledge();
        } catch (ListingsException ex) {
            log.warn(String.format("Error on validating listing event [%s]. Error message [%s]", listingEvent, ex.getMessage()));
            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error(String.format("Error occurred. Error message [%s]", ex.getMessage()), ex);
            acknowledgment.nack(Duration.ofMillis(5));
        }

    }

    /**
     * Checks if the listing with the provided ID exists and deletes it if it does.
     *
     * @param listingId - Listing id.
     */
    private void doDeleteListing(@NonNull String listingId) {
        if (!listingRepository.existsById(listingId)) {
            log.info(String.format("No listing found with id [%s]", listingId));
            return;
        }
        log.info(String.format("Deleting listing with id [%s]", listingId));
        listingRepository.deleteById(listingId);
    }

    /**
     * Saves the new listing to the database.
     *
     * @param listing - {@link Listing} object.
     */
    private void doCreateListing(@NonNull Listing listing) {
        log.info(String.format("Saving new listing [%s]", listing));
        listing.setDateCreated(LocalDate.now());
        Listing savedListing = listingRepository.save(listing, RefreshPolicy.IMMEDIATE);
        log.info(String.format("Saved a new listing [%s]", savedListing));
    }

    /**
     * Checks if the listing with the provided ID exists and updates it if it does.
     *
     * @param listing - {@link Listing} object.
     */
    private void doUpdateListing(@NonNull Listing listing) {
        log.info(String.format("Finding listing with id [%s]", listing.getId()));
        Optional<Listing> optionalListing = listingRepository.findById(listing.getId());
        if (optionalListing.isEmpty()) {
            log.info(String.format("Listing with id [%s] not found", listing.getId()));
            return;
        }

        Listing oldListing = optionalListing.get();

        log.info(String.format("Merging old listing [%s] with the new one [%s]", oldListing, listing));
        mergeListings(oldListing, listing);
        log.info(String.format("Merged listings [%s]", oldListing));

        log.info(String.format("Updating listing [%s]", oldListing));
        Listing savedListing = listingRepository.save(oldListing, RefreshPolicy.IMMEDIATE);
        log.info(String.format("Updated the listing [%s]", savedListing));
    }

    /**
     * Merges the values from the new listing to the old one that will be updated in the database.
     *
     * @param oldListing - {@link Listing} object from the database.
     * @param newListing - {@link Listing} object from the topic to be used to update the old one.
     */
    private void mergeListings(@NonNull Listing oldListing, @NonNull Listing newListing) {
        oldListing.setMake(newListing.getMake());
        oldListing.setModel(newListing.getModel());
        oldListing.setProductionYear(newListing.getProductionYear());
        oldListing.setMileage(newListing.getMileage());
        oldListing.setTransmissionType(newListing.getTransmissionType());
        oldListing.setFuelType(newListing.getFuelType());
        oldListing.setContactInfo(newListing.getContactInfo());
        oldListing.setDateUpdated(LocalDate.now());
    }

    /**
     * Validates if all the fields in the {@link Listing} object are present.
     * Throws {@link ListingsException} if any validation fails.
     *
     * @param listing - {@link Listing} object to validate.
     * @param mode    - {@link ListingEventMode} event mode.
     */
    private void validateListing(@NonNull Listing listing, ListingEventMode mode) {
        if (mode == null) {
            throw new ListingsException("No ListingEventMode provided");
        }

        if ((mode.equals(ListingEventMode.UPDATE)
                || mode.equals(ListingEventMode.DELETE))
                && !StringUtils.hasText(listing.getId())) {
            throw new ListingsException(String.format("No listing id provided for [%s] ListingEventMode", mode));
        }

        if (mode.equals(ListingEventMode.CREATE) || mode.equals(ListingEventMode.UPDATE)) {
            if (!StringUtils.hasText(listing.getMake())) {
                throw new ListingsException("No make provided.");
            }

            if (!StringUtils.hasText(listing.getModel())) {
                throw new ListingsException("No model provided.");
            }

            if (listing.getProductionYear() == null) {
                throw new ListingsException("No production year provided.");
            }

            if (listing.getProductionYear() < 1900) {
                throw new ListingsException(String.format("No production year before 1900 allowed. Provided value is [%s]", listing.getProductionYear()));
            }

            if (listing.getProductionYear() > LocalDate.now().getYear()) {
                throw new ListingsException(String.format("No production year larger than current year allowed. Provided value is [%s]", listing.getProductionYear()));
            }

            if (listing.getMileage() == null) {
                throw new ListingsException("No mileage provided.");
            }

            if (listing.getMileage() < 0) {
                throw new ListingsException(String.format("No negative mileage allowed. Provided value is [%s]", listing.getMileage()));
            }

            if (listing.getTransmissionType() == null) {
                throw new ListingsException("No transmission type provided.");
            }

            if (listing.getFuelType() == null) {
                throw new ListingsException("No fuel type provided.");
            }

            if (listing.getContactInfo() == null) {
                throw new ListingsException("No contact info provided.");
            }

            if (!StringUtils.hasText(listing.getContactInfo().getFirstName())) {
                throw new ListingsException("No first name provided in the contact info.");
            }

            if (!StringUtils.hasText(listing.getContactInfo().getLastName())) {
                throw new ListingsException("No last name provided in the contact info.");
            }

            if (!StringUtils.hasText(listing.getContactInfo().getEmail())) {
                throw new ListingsException("No email provided in the contact info.");
            }

            if (!StringUtils.hasText(listing.getContactInfo().getPhoneNumber())) {
                throw new ListingsException("No phone number provided in the contact info.");
            }
        }
    }
}
