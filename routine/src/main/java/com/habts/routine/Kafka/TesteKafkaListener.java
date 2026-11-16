package com.habts.routine.Kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class TesteKafkaListener {
    @KafkaListener(topics = "teste-topic", groupId = "teste-group")
    public void escutar(String mensagem) {
        System.out.println("Mensagem recebida do Kafka: " + mensagem);
    }
}
