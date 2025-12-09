package com.revhub.feedservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revhub.feedservice.dto.PostEvent;
import com.revhub.feedservice.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component
public class PostEventConsumer {

    @Autowired
    private FeedService feedService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "post-events", groupId = "feed-service-group")
    public void handlePostEvent(String message) {
        try {
            PostEvent postEvent = objectMapper.readValue(message, PostEvent.class);
            
            switch (postEvent.getEventType()) {
                case "CREATE":
                    feedService.processNewPost(postEvent);
                    break;
                case "DELETE":
                    feedService.removePostFromFeeds(postEvent.getPostId());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing post event: " + e.getMessage());
        }
    }
}