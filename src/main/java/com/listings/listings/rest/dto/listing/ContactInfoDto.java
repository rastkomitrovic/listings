package com.listings.listings.rest.dto.listing;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ContactInfoDto {

    @NotEmpty(message = "Contact info first name must be provided.")
    private String firstName;

    @NotEmpty(message = "Contact info last name must be provided.")
    private String lastName;

    @NotEmpty(message = "Contact info email must be provided.")
    private String email;

    @NotEmpty(message = "Contact info phone number must be provided.")
    private String phoneNumber;
}
