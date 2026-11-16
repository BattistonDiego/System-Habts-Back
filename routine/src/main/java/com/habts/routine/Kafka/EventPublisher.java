package com.habts.routine.Kafka;

public interface EventPublisher {
    void publish(HistoricoRegistradoEvento evento);
}
