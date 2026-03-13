package com.post_hub.iam_service.kafka.models.utils;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UtilMessage implements Serializable {
    private Long userId;
    private ActionType actionType;
    private PriorityType priorityType;
    private PostHubService service;
    private String message;
}
