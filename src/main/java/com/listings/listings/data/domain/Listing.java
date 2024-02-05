package com.listings.listings.data.domain;

import com.listings.listings.util.ElasticSearchConstants;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(indexName = ElasticSearchConstants.LISTINGS_INDEX_NAME)
public class Listing {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Keyword)
    private String make;
    @Field(type = FieldType.Keyword)
    private String model;
    @Field(type = FieldType.Integer)
    private Integer productionYear;
    @Field(type = FieldType.Long)
    private Long mileage;
    @Field(type = FieldType.Keyword)
    private TransmissionType transmissionType;
    @Field(type = FieldType.Keyword)
    private FuelType fuelType;
    @Field(type = FieldType.Object)
    private ContactInfo contactInfo;
    @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "dd.MM.uuuu")
    private LocalDate dateCreated;
    @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "dd.MM.uuuu")
    private LocalDate dateUpdated;
}
