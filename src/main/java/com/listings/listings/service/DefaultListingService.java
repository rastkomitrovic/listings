package com.listings.listings.service;

import com.listings.listings.data.domain.ContactInfo;
import com.listings.listings.data.domain.FuelType;
import com.listings.listings.data.domain.Listing;
import com.listings.listings.data.domain.TransmissionType;
import com.listings.listings.data.repositories.ListingRepository;
import com.listings.listings.kafka.domain.ListingEvent;
import com.listings.listings.kafka.domain.ListingEventMode;
import com.listings.listings.kafka.producer.ListingsTopicProducer;
import com.listings.listings.mapper.ListingMapper;
import com.listings.listings.rest.dto.listing.*;
import com.listings.listings.util.CacheConstants;
import com.listings.listings.util.ElasticSearchConstants;
import com.listings.listings.util.ListingsUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = {CacheConstants.ALL_LISTINGS_CACHE, CacheConstants.SEARCH_LISTINGS_CACHE})
public class DefaultListingService implements ListingService {

    private final ElasticsearchOperations elasticsearchOperations;

    private final ListingRepository listingRepository;

    private final ListingMapper listingMapper;

    private final ListingsTopicProducer listingsTopicProducer;

    private final Random random = new Random();

    @Override
    @Cacheable(value = {CacheConstants.ALL_LISTINGS_CACHE}, key = "{#page,#size,#sort,#sortDirection}")
    public ListingPage getAll(@NonNull Integer page, @NonNull Integer size, @NonNull ListingField sort, @NonNull Sort.Direction sortDirection) {
        log.info(String.format("Finding all listings by page number [%s], page size [%s], sort parameter [%s] and sort direction [%s]", page, size, sort, sortDirection));
        Page<Listing> listingsPage = listingRepository.findAll(ListingsUtils.createPageRequest(page, size, sort.getValue(), sortDirection));
        return ListingPage
                .builder()
                .page(page)
                .size(size)
                .sort(sort)
                .sortDirection(sortDirection)
                .totalElements(listingsPage.getTotalElements())
                .totalPages(listingsPage.getTotalPages())
                .content(
                        listingMapper.mapToListingsDtos(listingsPage.getContent())
                )
                .build();
    }

    @Override
    @Cacheable(value = {CacheConstants.SEARCH_LISTINGS_CACHE}, key = "{#searchListingsDto,#page,#size,#sort,#sortDirection}")
    public ListingPage searchAll(@NonNull SearchListingDto searchListingsDto, @NonNull Integer page, @NonNull Integer size, @NonNull ListingField sort, @NonNull Sort.Direction sortDirection) {
        log.info(String.format("Finding all listings by search criteria [%s] page number [%s], page size [%s], sort parameter [%s] and sort direction [%s]", searchListingsDto, page, size, sort, sortDirection));

        PageRequest pageRequest = ListingsUtils.createPageRequest(page, size, sort.getValue(), sortDirection);
        CriteriaQuery criteriaQuery = buildSearchQuery(searchListingsDto, pageRequest);

        log.info(String.format("Searching listings by page request [%s] and criteria query [%s]", pageRequest, criteriaQuery.getCriteria()));
        SearchHits<Listing> listings = elasticsearchOperations
                .search(
                        criteriaQuery,
                        Listing.class,
                        IndexCoordinates.of(ElasticSearchConstants.LISTINGS_INDEX_NAME)
                );


        SearchPage<Listing> listingsPage = SearchHitSupport.searchPageFor(listings, pageRequest);

        return ListingPage
                .builder()
                .page(page)
                .size(size)
                .sort(sort)
                .sortDirection(sortDirection)
                .totalElements(listingsPage.getTotalElements())
                .totalPages(listingsPage.getTotalPages())
                .content(
                        listingMapper.mapToListingsDtos(
                                listings
                                        .stream()
                                        .map(SearchHit::getContent)
                                        .toList()
                        )
                )
                .build();
    }

    @Override
    public void produceListingEvent(@NonNull ListingEventDto listingEventDto, @NonNull ListingEventMode mode) {
        ListingEvent listingEvent = listingMapper.mapToListingEvent(listingEventDto, mode);
        listingsTopicProducer.produceListing(listingEvent);
    }

    @Override
    @Transactional
    public void generateRandomListings() {
        List<Listing> listings = new LinkedList<>();
        for (int i = 0; i < 40; i++) {
            String firstName = getRandomFirstName();
            String lastName = getRandomLastName();
            String make = getRandomMake();

            listings.add(Listing
                    .builder()
                    .make(make)
                    .model(getRandomModel(make))
                    .productionYear(getRandomProductionYear())
                    .mileage(getRandomMileage())
                    .transmissionType(getRandomTransmission())
                    .fuelType(getRandomFuelType())
                    .contactInfo(ContactInfo
                            .builder()
                            .firstName(firstName)
                            .lastName(lastName)
                            .email(firstName + lastName + "@" + getRandomEmailDomain())
                            .phoneNumber(getRandomPhoneNumber())
                            .build())
                    .dateCreated(LocalDate.now())
                    .build());
        }
        listingRepository.saveAll(listings);
    }

    /**
     * Creates the criteria query for searching listings based on the provided parameters in the {@link SearchListingDto} object.
     *
     * @param searchListingDto - {@link SearchListingDto} object representing the search criteria.
     * @param pageRequest      - {@link PageRequest} object for pagination.
     * @return - {@link CriteriaQuery} object that can be used with Spring repositories to search the listings.
     */
    private CriteriaQuery buildSearchQuery(@NonNull SearchListingDto searchListingDto, @NonNull PageRequest pageRequest) {
        Criteria criteria = new Criteria();

        for (SearchListingItemDto searchListingItem : searchListingDto.getSearchListingItems()) {
            criteria = criteria.and(new Criteria(searchListingItem.getField().getValue()).matches(searchListingItem.getValue()));
        }

        return new CriteriaQuery(criteria, pageRequest);
    }

    /**
     * Generates the random make.
     *
     * @return - {@link String} representing the random make.
     */
    private String getRandomMake() {
        String[] makes = {"Honda", "Toyota", "Ford", "Chevrolet", "BMW", "Mercedes-Benz", "Audi", "Volkswagen", "Nissan"};
        return makes[random.nextInt(makes.length)];
    }

    /**
     * Generates the random model based on the make.
     *
     * @param make - {@link String} representing the make.
     * @return - {@link String} representing the random model based on the make.
     */
    private String getRandomModel(@NonNull String make) {
        switch (make) {
            case "Honda" -> {
                String[] hondaModels = {"Civic", "Accord", "CR-V", "Pilot", "Fit", "HR-V"};
                return hondaModels[random.nextInt(hondaModels.length)];
            }
            case "Toyota" -> {
                String[] toyotaModels = {"Camry", "Corolla", "RAV4", "Highlander", "Tacoma", "Sienna"};
                return toyotaModels[random.nextInt(toyotaModels.length)];
            }
            case "Ford" -> {
                String[] fordModels = {"F-150", "Escape", "Focus", "Explorer", "Edge", "Mustang"};
                return fordModels[random.nextInt(fordModels.length)];
            }
            case "Chevrolet" -> {
                String[] chevroletModels = {"Silverado", "Equinox", "Cruze", "Malibu", "Traverse", "Tahoe"};
                return chevroletModels[random.nextInt(chevroletModels.length)];
            }
            case "BMW" -> {
                String[] bmwModels = {"3 Series", "5 Series", "X3", "X5", "X7", "M3"};
                return bmwModels[random.nextInt(bmwModels.length)];
            }
            case "Mercedes-Benz" -> {
                String[] mercedesModels = {"C-Class", "E-Class", "S-Class", "GLC", "GLE", "GLS"};
                return mercedesModels[random.nextInt(mercedesModels.length)];
            }
            case "Audi" -> {
                String[] audiModels = {"A3", "A4", "A6", "Q5", "Q7", "Q8"};
                return audiModels[random.nextInt(audiModels.length)];
            }
            case "Volkswagen" -> {
                String[] volkswagenModels = {"Golf", "Jetta", "Passat", "Tiguan", "Atlas", "Arteon"};
                return volkswagenModels[random.nextInt(volkswagenModels.length)];
            }
            case "Nissan" -> {
                String[] nissanModels = {"Altima", "Maxima", "Sentra", "Rogue", "Pathfinder", "Armada"};
                return nissanModels[random.nextInt(nissanModels.length)];
            }
            default -> {
                return "Unknown Model";
            }
        }
    }

    /**
     * Generates the random production year.
     *
     * @return - {@link Integer} representing the random production year.
     */
    private Integer getRandomProductionYear() {
        return 1990 + random.nextInt(32);
    }

    /**
     * Generates the random mileage.
     *
     * @return - {@link Long} representing the random mileage.
     */
    private Long getRandomMileage() {
        return random.nextLong(150000) + 10000;
    }

    /**
     * Generates the random transmission type.
     *
     * @return - {@link TransmissionType} representing the random transmission type.
     */
    private TransmissionType getRandomTransmission() {
        TransmissionType[] transmissionTypes = TransmissionType.values();
        return transmissionTypes[random.nextInt(transmissionTypes.length)];
    }

    /**
     * Generates the random fuel type.
     *
     * @return - {@link FuelType} representing the random fuel type.
     */
    private FuelType getRandomFuelType() {
        FuelType[] fuelTypes = FuelType.values();
        return fuelTypes[random.nextInt(fuelTypes.length)];
    }

    /**
     * Generates random first name.
     *
     * @return - {@link String} representing the first name.
     */
    private String getRandomFirstName() {
        String[] firstNames = {"John", "Emily", "Michael", "Emma", "William", "Olivia", "James", "Sophia", "Benjamin", "Isabella"};
        return firstNames[random.nextInt(firstNames.length)];
    }

    /**
     * Generates random last name.
     *
     * @return - {@link String} representing the random last name.
     */
    private String getRandomLastName() {
        String[] lastNames = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor"};
        return lastNames[random.nextInt(lastNames.length)];
    }

    /**
     * Generates the random email domain
     *
     * @return - {@link String} representing the random email domain.
     */
    private String getRandomEmailDomain() {
        String[] domains = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com"};
        return domains[random.nextInt(domains.length)];
    }

    /**
     * Generates the random phone number.
     *
     * @return - {@link String} representing the random phone number.
     */
    private String getRandomPhoneNumber() {
        StringBuilder phoneNumber = new StringBuilder("+");
        phoneNumber.append(1 + random.nextInt(9));
        for (int i = 0; i < 10; i++) {
            phoneNumber.append(random.nextInt(10));
        }
        return phoneNumber.toString();
    }

}
