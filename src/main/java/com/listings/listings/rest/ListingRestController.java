package com.listings.listings.rest;

import com.listings.listings.kafka.domain.ListingEventMode;
import com.listings.listings.rest.dto.error.ErrorResponse;
import com.listings.listings.rest.dto.listing.ListingEventDto;
import com.listings.listings.rest.dto.listing.ListingField;
import com.listings.listings.rest.dto.listing.ListingPage;
import com.listings.listings.rest.dto.listing.SearchListingDto;
import com.listings.listings.service.ListingService;
import com.listings.listings.util.RestConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = RestConstants.LISTINGS_BASE_API)
@RequiredArgsConstructor
@Slf4j
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Error in request data", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
})
public class ListingRestController {

    private final ListingService listingService;


    @Operation(summary = "Gets all the listings with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got the page of listings", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ListingPage.class)))}),
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ListingPage> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "dateCreated") ListingField sort,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") Sort.Direction sortDirection) {
        log.info(String.format("Got get all listings request. Page [%s], Size [%s]", page, size));
        return ResponseEntity.ok(listingService.getAll(page, size, sort, sortDirection));
    }

    @Operation(summary = "Gets all the listings with pagination based on the search criteria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got the page of listings", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ListingPage.class)))}),
    })
    @PostMapping(value = "/search", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ListingPage> searchAll(
            @RequestBody @NotNull @Valid SearchListingDto searchListingDto,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "dateCreated") ListingField sort,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") Sort.Direction sortDirection) {
        log.info(String.format("Got search all listings request. Page [%s], Size [%s], SearchListingDto [%s]", page, size, searchListingDto));
        return ResponseEntity.ok(listingService.searchAll(searchListingDto, page, size, sort, sortDirection));
    }

    @Operation(summary = "Produces a listing event.")
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully produced a listing event.")
    })
    public ResponseEntity<Void> produceListingEvent(@RequestBody @Valid ListingEventDto listingEventDto, @RequestParam ListingEventMode mode) {
        log.info(String.format("Got produce listing event request. Listing Dto [%s], Listing event mode [%s]", listingEventDto, mode));
        listingService.produceListingEvent(listingEventDto, mode);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Generates 40 random listings")
    @PostMapping("/random")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated random listings")
    })
    public ResponseEntity<Void> generateRandomListings() {
        log.info("Got generate random listings request");
        listingService.generateRandomListings();
        return ResponseEntity.ok().build();
    }
}
