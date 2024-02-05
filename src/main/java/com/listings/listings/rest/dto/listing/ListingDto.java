package com.listings.listings.rest.dto.listing;

import com.listings.listings.data.domain.FuelType;
import com.listings.listings.data.domain.TransmissionType;
import lombok.*;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ListingDto {
    private String id;
    private String make;
    private String model;
    private Integer productionYear;
    private Long mileage;
    private TransmissionType transmissionType;
    private FuelType fuelType;
    private ContactInfoDto contactInfo;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
}
