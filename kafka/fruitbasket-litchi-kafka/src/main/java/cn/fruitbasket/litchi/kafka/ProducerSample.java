package cn.fruitbasket.litchi.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

/**
 * 生产者例子
 *
 * @author LiuBing
 * @since 2021/9/17
 */
public class ProducerSample {

    /**
     * 定时生产消息
     *
     * @param brokerAddresses kafka 服务地址
     * @param topic           主题
     */
    public void producer(String brokerAddresses, String topic) {
        // 配置
        Properties p = new Properties();
        p.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddresses);
        // k、v序列化器
        p.setProperty(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.setProperty(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.setProperty(ACKS_CONFIG, "-1");
        KafkaProducer<String, String> producer = new KafkaProducer<>(p);

        List<String> kes = Arrays.asList("apple", "banana", "litchi");
        final int countEveryKey = 3;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                kes.parallelStream().forEach(key -> {
                    for (int i = 0; i < countEveryKey; i++) {
                        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, key + i);
                        Future<RecordMetadata> send = producer.send(record);

                        RecordMetadata rm;
                        try {
                            rm = send.get();
                            System.out.printf("生产返回数据————key:%s, val:%s, partition:%s, offset:%s%n",
                                    record.key(), record.value(), rm.partition(), rm.offset());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 0, 30);
        while (true) ;
    }
}
