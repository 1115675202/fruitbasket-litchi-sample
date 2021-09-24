package cn.fruitbasket.litchi.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Sample1Test {

    private Sample1 sample1;

    @BeforeEach
    void init() {
        this.sample1 = new Sample1();
    }


    @Test
    void producer() {
        this.sample1.producer();
    }

    /**
     * 提交粒度为每条记录
     */
    @Test
    void consumerCommitEachRecord() {
        this.sample1.consumer("g1", new EachRecord());
    }

    /**
     * 提交粒度为每个分区
     */
    @Test
    void consumerCommitEachPartition() {
        this.sample1.consumer("g1", new EachPartition());
    }

    /**
     * 提交粒度为每次拉取
     */
    @Test
    void consumerCommitEachPull() {
        this.sample1.consumer("g1", new EachPull());
    }
}