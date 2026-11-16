package com.habts.routine.Kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
@RequestMapping("/teste-kafka")
public class TesteKafkaController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TesteKafkaController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public String enviar(@RequestParam String mensagem) {
        kafkaTemplate.send("teste-topic", mensagem);
        return "Mensagem enviada: " + mensagem;
    }
}
