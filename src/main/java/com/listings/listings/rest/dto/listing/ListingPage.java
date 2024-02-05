package com.listings.listings.rest.dto.listing;

import lombok.*;
import org.springframework.data.domain.Sort;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ListingPage {

    private Integer page;
    private Integer size;
    private ListingField sort;
    private Sort.Direction sortDirection;
    private Long totalElements;
    private Integer totalPages;
    private List<ListingDto> content;
}
