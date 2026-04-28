package com.ias.transfer.application.port.out;

public interface EventPublisher {
    void publish(Object event);
}
