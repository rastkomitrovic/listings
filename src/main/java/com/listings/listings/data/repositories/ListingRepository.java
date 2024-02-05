package com.listings.listings.data.repositories;

import com.listings.listings.data.domain.Listing;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingRepository extends ElasticsearchRepository<Listing, String> {
}
