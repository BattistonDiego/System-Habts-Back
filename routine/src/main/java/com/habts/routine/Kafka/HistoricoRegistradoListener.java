package com.habts.routine.Kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class HistoricoRegistradoListener {
    @KafkaListener(topics = "historico-registrado", groupId = "habits-service")
    public void escutar(HistoricoRegistradoEvento evento) {
        System.out.println("Evento recebido: " + evento);
    }

}
