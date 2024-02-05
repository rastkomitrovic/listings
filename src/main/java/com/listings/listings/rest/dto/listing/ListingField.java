package com.listings.listings.rest.dto.listing;

public enum ListingField {

    ID("id"),
    MAKE("make"),
    MODEL("model"),
    PRODUCTION_YEAR("productionYear"),
    MILEAGE("mileage"),
    TRANSMISSION_TYPE("transmissionType"),
    FUEL_TYPE("fuelType"),
    DATE_CREATED("dateCreated"),
    DATE_UPDATED("dateUpdated"),
    CONTACT_FIRST_NAME("contactInfo.firstName"),
    CONTACT_LAST_NAME("contactInfo.lastName"),
    CONTACT_EMAIL("contactInfo.email"),
    CONTACT_PHONE_NUMBER("contactInfo.phoneNumber");

    private String value;

    ListingField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
