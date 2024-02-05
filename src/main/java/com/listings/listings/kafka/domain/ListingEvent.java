package com.listings.listings.kafka.domain;


import com.listings.listings.data.domain.FuelType;
import com.listings.listings.data.domain.TransmissionType;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ListingEvent {

    private String id;
    private String make;
    private String model;
    private Integer productionYear;
    private Long mileage;
    private TransmissionType transmissionType;
    private FuelType fuelType;
    private ListingEventContactInfo contactInfo;
    private ListingEventMode mode;
}
