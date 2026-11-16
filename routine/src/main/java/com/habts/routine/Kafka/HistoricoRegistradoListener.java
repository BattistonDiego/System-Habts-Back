package com.habts.routine.Kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class HistoricoRegistradoListener {
    @KafkaListener(topics = "historico-registrado", groupId = "habits-service")
    public void escutar(HistoricoRegistradoEvento evento) {
        System.out.println("Evento recebido: " + evento);
    }

}
