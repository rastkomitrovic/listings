package com.listings.listings.rest.dto.listing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SearchListingDto {

    @NotNull(message = "Search items must be provided.")
    @Valid
    private List<SearchListingItemDto> searchListingItems;
}
