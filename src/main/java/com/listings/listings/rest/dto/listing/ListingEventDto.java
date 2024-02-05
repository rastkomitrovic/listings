package com.listings.listings.rest.dto.listing;

import com.listings.listings.data.domain.FuelType;
import com.listings.listings.data.domain.TransmissionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ListingEventDto {
    private String id;

    @NotEmpty(message = "Listing make must not be provided.")
    private String make;

    @NotEmpty(message = "Listing model must not be provided.")
    private String model;

    @NotNull(message = "Listing production year must be provided.")
    @Min(value = 1900, message = "Listing production year can have a minimum value of 1900.")
    private Integer productionYear;

    @NotNull(message = "Listing mileage must be provided")
    @Min(value = 0, message = "Listing mileage can have a minimum value of 0.")
    private Long mileage;

    @NotNull(message = "Listing transmission type must be provided.")
    private TransmissionType transmissionType;

    @NotNull(message = "Listing fuel type must be provided.")
    private FuelType fuelType;

    @NotNull(message = "Listing contact info must be provided.")
    @Valid
    private ContactInfoDto contactInfo;

}
