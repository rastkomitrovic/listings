package com.listings.listings.rest.dto.error;

import java.util.List;

public record ErrorResponse(List<String> messages) {
}
