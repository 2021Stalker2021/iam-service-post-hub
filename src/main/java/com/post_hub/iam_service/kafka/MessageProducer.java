package com.post_hub.iam_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.post_hub.iam_service.kafka.models.utils.PostHubService;
import com.post_hub.iam_service.kafka.models.utils.UtilMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
// Отправитель kafka сообщений(producer), отвечает за всю логику отправки сообщений в kafka
public class MessageProducer {

    // для конвертации Java объектов в JSON и наоборот
    private final ObjectMapper objectMapper;
    // для отправления уведомлений в kafka топике
    private final KafkaTemplate<String, String> kafkaTemplate;

    // имя kafka топика который будет отправлять логи от IamService
    @Value(value = "${additional.kafka.topic.iam.service.logs}")
    private String logsOutTopic;

    // флаг, включена ли отправка сообщений в kafka
    @Value(value = "${kafka.enabled}")
    private boolean isKafkaEnabled;

    public void sendLogs(@NotNull @Valid UtilMessage message) {
        if (!isKafkaEnabled) { // если false
            log.trace("Kafka is not enabled. Message will not be placed im iam_logs topic [message={}] ", message);
            return;
        }
        try {
            message.setService(PostHubService.IAM_SERVICE); // от какого сервиса пришло сообщение
            String messageJson = objectMapper.writeValueAsString(message); // from Java Object to JSON
            log.debug("Sending message to Kafka: {}", messageJson);
            kafkaTemplate.send(logsOutTopic, messageJson).get(); // отправляем сообщение в указанный топик
            log.debug("Kafka {} message sent. Topic: '{}', message: '{}'", message.getActionType(), logsOutTopic, messageJson);
        } catch (Exception cause) {
            log.error("Kafka message didn't send. ", cause);
        }
    }
}
