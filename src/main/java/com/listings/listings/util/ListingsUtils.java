package com.listings.listings.util;

import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ListingsUtils {

    private ListingsUtils() {

    }

    /**
     * Creates the page request object for pagination search.
     *
     * @param page          - Page number.
     * @param size          - Page size.
     * @param sort          - Sort param.
     * @param sortDirection - Sort direction.
     * @return {@link PageRequest} object.
     */
    public static PageRequest createPageRequest(@NonNull Integer page, @NonNull Integer size, @NonNull String sort, @NonNull Sort.Direction sortDirection) {
        return PageRequest.of(page, size, Sort.by(sortDirection, sort));
    }
}
