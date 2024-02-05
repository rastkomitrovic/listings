package com.listings.listings.rest.dto.listing;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SearchListingItemDto {

    @NotNull(message = "Search field must be provided.")
    private ListingField field;

    @NotNull(message = "Search value must be provided.")
    private String value;
}
