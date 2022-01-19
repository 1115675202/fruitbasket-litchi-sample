package cn.fruitbasket.litchi.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProducerSampleTest {

    /**
     * 执行前先在任意broker服务器创建名为test的topic，指明使用2个分区和2个副本
     * kafka-topics.sh --bootstrap-server node1:9092 --create --topic test --partitions 2 --replication-factor 2
     */
    static final String TOPIC = "test";

    static final String BROKER_ADDRESSES = "node1:9092,node2:9092,node3:9092";

    private ProducerSample producer;

    @BeforeEach
    void init() {
        this.producer = new ProducerSample();
    }

    @Test
    void producer() {
        this.producer.producer(BROKER_ADDRESSES, TOPIC);
    }
}