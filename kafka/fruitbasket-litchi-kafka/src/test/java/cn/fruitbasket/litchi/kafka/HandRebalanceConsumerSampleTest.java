package cn.fruitbasket.litchi.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static cn.fruitbasket.litchi.kafka.ProducerSampleTest.BROKER_ADDRESSES;
import static cn.fruitbasket.litchi.kafka.ProducerSampleTest.TOPIC;

class HandRebalanceConsumerSampleTest {

    private HandRebalanceConsumerSample handRebalanceConsumerSample;

    @BeforeEach
    void init() {
        this.handRebalanceConsumerSample = new HandRebalanceConsumerSample();
    }

    @Test
    void consumer() {
        this.handRebalanceConsumerSample.consume(BROKER_ADDRESSES, TOPIC, "g1");
    }
}