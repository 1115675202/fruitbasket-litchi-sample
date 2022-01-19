package cn.fruitbasket.litchi.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static cn.fruitbasket.litchi.kafka.ProducerSampleTest.BROKER_ADDRESSES;
import static cn.fruitbasket.litchi.kafka.ProducerSampleTest.TOPIC;

class ConsumerCommitSampleTest {

    private static final String CONSUMER_GROUP = "g1";

    private DiffCommitConsumerSample consumerCommitSample;

    @BeforeEach
    void init() {
        this.consumerCommitSample = new DiffCommitConsumerSample();
    }

    /**
     * 提交粒度为每条记录
     */
    @Test
    void consumerCommitEachRecord() {
        this.consumerCommitSample.consume(BROKER_ADDRESSES, TOPIC, CONSUMER_GROUP, new EachRecord());
    }

    /**
     * 提交粒度为每个分区
     */
    @Test
    void consumerCommitEachPartition() {
        this.consumerCommitSample.consume(BROKER_ADDRESSES, TOPIC, CONSUMER_GROUP, new EachPartition());
    }

    /**
     * 提交粒度为每次拉取
     */
    @Test
    void consumerCommitEachPull() {
        this.consumerCommitSample.consume(BROKER_ADDRESSES, TOPIC, CONSUMER_GROUP, new EachPull());
    }
}