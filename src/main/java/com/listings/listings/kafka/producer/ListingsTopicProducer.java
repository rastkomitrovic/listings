package com.listings.listings.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.listings.listings.kafka.domain.ListingEvent;
import com.listings.listings.util.KafkaConstants;
import com.listings.listings.util.ListingsException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListingsTopicProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;


    /**
     * Produces a listing event to the listings topic.
     *
     * @param listingEvent - {@link ListingEvent} object.
     */
    public void produceListing(@NonNull ListingEvent listingEvent) {
        try {
            log.info(String.format("Sending a new listing event to listings topic [%s]", listingEvent));
            SendResult<String, String> sendResult = kafkaTemplate.send(KafkaConstants.LISTINGS_TOPIC, objectMapper.writeValueAsString(listingEvent)).get();
            log.info(String.format("Successfully sent a listing event [%s] to [%s] topic. Send result [%s]", listingEvent, KafkaConstants.LISTINGS_TOPIC, sendResult));
        } catch (Exception ex) {
            log.error(String.format("Error on sending the message to the topic. Error message [%s]", ex.getMessage()), ex);
            throw new ListingsException(String.format("Error on sending the message to the listings topic. Error message [%s]", ex.getMessage()));
        }
    }
}
