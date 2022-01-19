package cn.fruitbasket.litchi.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

/**
 * 再均衡处理列子
 *
 * @author LiuBing
 * @since 2021/9/17
 */
public class HandRebalanceConsumerSample {

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    /**
     * @param brokerAddresses kafka 地址
     * @param topic           主题
     * @param consumerGroup   消费者归属的消费组
     */
    public void consume(String brokerAddresses, String topic, String consumerGroup) {
        Properties p = new Properties();
        p.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddresses);
        p.setProperty(GROUP_ID_CONFIG, consumerGroup);
        p.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.setProperty(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.setProperty(ENABLE_AUTO_COMMIT_CONFIG, "false");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(p);

        ConsumerRebalanceListener rebalanceListener = new ConsumerRebalanceListener() {

            /**
             * 方法会在消费者停止读取消息之后和再均衡开始之前被调用。
             *
             * @param collection 拥有的分区集合
             */
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                // 如果在这里提交偏移量，下一个接管分区 的消费者就知道该从哪里开始读取了
                consumer.commitSync(currentOffsets);
            }

            /**
             * 方法会在重新分配分区之后和消费者开始读取消息之前被调用
             *
             * @param collection 拥有的分区集合
             */
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                // 清除之前状态。。。
            }
        };

        try {
            consumer.subscribe(Collections.singleton(topic), rebalanceListener);
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ZERO);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("消费的分区信息————partition:%s, offset:%s, key:%s, value:%s%n",
                            record.partition(), record.offset(), record.key(), record.value());

                    this.currentOffsets.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1));
                }
                consumer.commitAsync(this.currentOffsets, null);
            }
        } catch (Exception e) {
            // 忽略或者记录日志
        } finally {
            try {
                consumer.commitSync(this.currentOffsets);
            } finally {
                consumer.close();
            }
        }
    }
}
