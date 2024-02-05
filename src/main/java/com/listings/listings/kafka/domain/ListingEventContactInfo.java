package com.listings.listings.kafka.domain;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ListingEventContactInfo {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
