package cn.fruitbasket.litchi.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

/**
 * @author LiuBing
 * @since 2021/9/17
 */
public class DiffCommitConsumerSample {

    /**
     * @param brokerAddresses kafka 服务地址
     * @param topic           主题
     * @param consumerGroup   消费者归属的消费组
     * @param consumerTask    不同消费方式
     */
    public void consume(String brokerAddresses, String topic, String consumerGroup, ConsumerTask consumerTask) {
        Properties p = new Properties();
        p.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddresses);
        // 消费组，同一条消息在一个组里面只有一个消费者能消费到
        p.setProperty(GROUP_ID_CONFIG, consumerGroup);
        // k、v反序列化器
        p.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.setProperty(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // 当Kafka没有找到当前消费的offset（第一次启动，或者offset数据被删掉了）时使用的策略
        // earliest：从最开始的offset
        // latest：从最后的offset(默认)
        // none：抛出异常
        p.setProperty(AUTO_OFFSET_RESET_CONFIG, "latest");

        // offset提交方式
        // true：自动提交，用异步的方式间隔时间后提交，需要搭配AUTO_COMMIT_INTERVAL_MS_CONFIG。
        // 还没消费完就到时间提交了，然后消费异常导致不能重试消费这些数据，因为offset已经往后更新了，导致消息丢失
        // 消费太快还没自动提交，然后消费者挂了，重启又从之前的位置开始消费，导致重复消费
        // false：手动提交，需要编写手动提交代码
        p.setProperty(ENABLE_AUTO_COMMIT_CONFIG, "false");
        // 自动提交时间间隔默认5秒
//        p.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"15000");

        // 一次向broker拉取数据的最大数量，配置这个按需、按消费能力拉取
//        p.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "50");


        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(p);
        // 同一组消费者数量变更会动态给它们分配分区，监听器可以监听到这个事件
        // 比如新上线一个消费者，会给它分配分区触发onPartitionsAssigned
        // 之前消费者会被取消所有分配的分区触发onPartitionsRevoked，同时也会重新分配新分区触发onPartitionsAssigned
        ConsumerRebalanceListener listener = new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                System.out.println("onPartitionsRevoked:" + partitions.stream().map(TopicPartition::partition).collect(toList()));
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                System.out.println("onPartitionsAssigned:" + partitions.stream().map(TopicPartition::partition).collect(toList()));
            }
        };

        consumer.subscribe(Collections.singleton(topic), listener);
        new Timer().schedule(consumerTask.setConsumer(consumer), 0, 3000);
        while (true) ;
    }
}

abstract class ConsumerTask extends TimerTask {

    protected KafkaConsumer<String, String> consumer;

    public ConsumerTask setConsumer(KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;
        return this;
    }
}

/**
 * 每消费一条记录提交一次，可靠性高，但是提交频繁性能较低
 */
class EachRecord extends ConsumerTask {

    @Override
    public void run() {
        /*
            拉取数据，参数是没有数据的时候阻塞等待的时间，0代表一直阻塞
            一个分区只能由一个消费者消费，但是一个消费者可以消费多个分区，所以这里是一个集合，来自不同的分区
         */
        ConsumerRecords<String, String> records = consumer.poll(Duration.ZERO);
        for (ConsumerRecord<String, String> record : records) {
            System.out.printf("消费的分区信息————partition:%s, offset:%s, key:%s, value:%s%n",
                    record.partition(), record.offset(), record.key(), record.value());

            Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
            offsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset()));
            consumer.commitSync(offsets);
        }
    }
}

/**
 * 按分区提交，不同分区可以用不同的线程处理，不同分区消费进度是分开保存互不影响的
 */
class EachPartition extends ConsumerTask {

    @Override
    public void run() {
        // 拉取数据，参数是没有数据的时候阻塞等待的时间，0代表一直阻塞
        // 一个分区只能由一个消费者消费，但是一个消费者可以消费多个分区，所以这里是一个集合，来自不同的分区
        ConsumerRecords<String, String> records = consumer.poll(Duration.ZERO);
        Set<TopicPartition> partitions = records.partitions();
        // 不同的分区并行处理
        partitions.parallelStream().forEach(partition -> {
            List<ConsumerRecord<String, String>> crs = records.records(partition);

            // 同一分区也可以并行处理，最终提交最大的offset就行，如果需要保证顺序消费这里就应该串行
            crs.parallelStream().forEach(cr ->
                    System.out.printf("消费的分区信息————partition:%s, offset:%s, key:%s, value:%s%n",
                            cr.partition(), cr.offset(), cr.key(), cr.value()));

            Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
            long offset = crs.get(crs.size() - 1).offset();
            offsets.put(partition, new OffsetAndMetadata(offset));
            consumer.commitSync(offsets);
        });
    }
}

/**
 * 每次拉取一起提交，提交粒度大
 */
class EachPull extends ConsumerTask {

    @Override
    public void run() {
        // 拉取数据，参数是没有数据的时候阻塞等待的时间，0代表一直阻塞
        // 一个分区只能由一个消费者消费，但是一个消费者可以消费多个分区，所以这里是一个集合，来自不同的分区
        ConsumerRecords<String, String> records = consumer.poll(Duration.ZERO);
        for (ConsumerRecord<String, String> record : records) {
            System.out.printf("消费的分区信息————partition:%s, offset:%s, key:%s, value:%s%n",
                    record.partition(), record.offset(), record.key(), record.value());
        }
        consumer.commitSync();
    }
}



