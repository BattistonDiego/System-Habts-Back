package com.habts.routine.Kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaEventPublisher implements EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);
    private final KafkaTemplate<String, HistoricoRegistradoEvento> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, HistoricoRegistradoEvento> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(HistoricoRegistradoEvento evento) {
        try {
            kafkaTemplate.send("historico-registrado", evento.getUsuarioId().toString(), evento)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.warn("Kafka indisponível, evento não enviado", error);
                        }
                    });
        } catch (Exception ex) {
            log.warn("Kafka indisponível, evento não enviado", ex);
        }
    }
}
