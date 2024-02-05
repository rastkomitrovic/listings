package com.listings.listings.data.domain;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ContactInfo {

    @Field(type = FieldType.Keyword)
    private String firstName;
    @Field(type = FieldType.Keyword)
    private String lastName;
    @Field(type = FieldType.Keyword)
    private String email;
    @Field(type = FieldType.Keyword)
    private String phoneNumber;

}
