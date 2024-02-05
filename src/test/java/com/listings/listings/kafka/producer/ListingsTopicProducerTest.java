package com.listings.listings.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.listings.listings.kafka.domain.ListingEvent;
import com.listings.listings.util.KafkaConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListingsTopicProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ListingsTopicProducer listingsTopicProducer;

    @Test
    public void voidTestProduceListing() throws JsonProcessingException, ExecutionException, InterruptedException {
        ListingEvent listingEvent = ListingEvent.builder().build();
        String json = "{}";
        when(objectMapper.writeValueAsString(listingEvent)).thenReturn(json);
        SendResult<String, String> sendResult = Mockito.mock(SendResult.class);
        CompletableFuture<SendResult<String, String>> completableFuture = Mockito.mock(CompletableFuture.class);
        when(completableFuture.get()).thenReturn(sendResult);
        when(kafkaTemplate.send(KafkaConstants.LISTINGS_TOPIC, json)).thenReturn(completableFuture);

        assertDoesNotThrow(() -> listingsTopicProducer.produceListing(listingEvent));

        verify(objectMapper, times(1)).writeValueAsString(listingEvent);
        verify(kafkaTemplate, times(1)).send(KafkaConstants.LISTINGS_TOPIC, json);
    }
}
