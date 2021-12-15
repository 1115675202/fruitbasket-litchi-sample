package cn.fruitbasket.litchi.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsumerCommitSampleTest {

    private ConsumerCommitSample consumerCommitSample;

    @BeforeEach
    void init() {
        this.consumerCommitSample = new ConsumerCommitSample();
    }


    @Test
    void producer() {
        this.consumerCommitSample.producer();
    }

    /**
     * 提交粒度为每条记录
     */
    @Test
    void consumerCommitEachRecord() {
        this.consumerCommitSample.consumer("g1", new EachRecord());
    }

    /**
     * 提交粒度为每个分区
     */
    @Test
    void consumerCommitEachPartition() {
        this.consumerCommitSample.consumer("g1", new EachPartition());
    }

    /**
     * 提交粒度为每次拉取
     */
    @Test
    void consumerCommitEachPull() {
        this.consumerCommitSample.consumer("g1", new EachPull());
    }
}