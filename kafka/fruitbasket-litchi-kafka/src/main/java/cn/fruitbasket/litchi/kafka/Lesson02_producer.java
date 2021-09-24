package cn.fruitbasket.litchi.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
/**
 * @author LiuBing
 * @date 2021/9/17
 */
public class Lesson02_producer {

    public static String brokers = "node01:9092,node02:9092,node03:9092";
    public static Properties initConf(){
        Properties conf = new Properties();
        conf.setProperty(ProducerConfig.ACKS_CONFIG,"0");
        conf.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        conf.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        conf.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,brokers);
        conf.setProperty(ProducerConfig.PARTITIONER_CLASS_CONFIG, DefaultPartitioner.class.getName());

        conf.setProperty(ProducerConfig.BATCH_SIZE_CONFIG,"16384"); //16k 要调整的,分析我们msg的大小，尽量触发批次发送，减少内存碎片，和系统调用的复杂度
        conf.setProperty(ProducerConfig.LINGER_MS_CONFIG,"0");  //


        conf.setProperty(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,"1048576");
        //message.max.bytes


        conf.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG,"33554432");//32M
        conf.setProperty(ProducerConfig.MAX_BLOCK_MS_CONFIG,"60000"); //60秒

        conf.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,"5");

        conf.setProperty(ProducerConfig.SEND_BUFFER_CONFIG,"32768");  //32K   -1
        conf.setProperty(ProducerConfig.RECEIVE_BUFFER_CONFIG,"32768"); //32k  -1
        return conf;
    }

    private static final String ACKS_DOC = "The number of acknowledgments the producer requires the leader to have received before considering a request complete. This controls the "
            + " durability of records that are sent. The following settings are allowed: "
            + " <ul>"
            + " <li><code>acks=0</code> If set to zero then the producer will not wait for any acknowledgment from the"
            + " server at all. The record will be immediately added to the socket buffer and considered sent. No guarantee can be"
            + " made that the server has received the record in this case, and the <code>retries</code> configuration will not"
            + " take effect (as the client won't generally know of any failures). The offset given back for each record will"
            + " always be set to <code>-1</code>."
            + " <li><code>acks=1</code> This will mean the leader will write the record to its local log but will respond"
            + " without awaiting full acknowledgement from all followers. In this case should the leader fail immediately after"
            + " acknowledging the record but before the followers have replicated it then the record will be lost."
            + " <li><code>acks=all</code> This means the leader will wait for the full set of in-sync replicas to"
            + " acknowledge the record. This guarantees that the record will not be lost as long as at least one in-sync replica"
            + " remains alive. This is the strongest available guarantee. This is equivalent to the acks=-1 setting."
            + "</ul>";

    public static void main(String[] args) throws ExecutionException, InterruptedException {


        System.out.println(ACKS_DOC);
//
//        Properties conf = initConf();
//        KafkaProducer<String, String> producer = new KafkaProducer<>(conf);
//
//        while (true) {
//            ProducerRecord<String, String> msg = new ProducerRecord<String, String>("ooxx", "hello", "ooxx1");
//
//            Future<RecordMetadata> future = producer.send(msg);
//            RecordMetadata recordMetadata = future.get();
//
//        }
//        Future<RecordMetadata> send = producer.send(msg, new Callback() {
//            @Override
//            public void onCompletion(RecordMetadata metadata, Exception exception) {
//
//            }
//        });
    }
}

