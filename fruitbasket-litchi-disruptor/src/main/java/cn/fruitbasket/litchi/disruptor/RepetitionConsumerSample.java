package cn.fruitbasket.litchi.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static com.lmax.disruptor.dsl.ProducerType.SINGLE;

/**
 * 一条消息多个消费者重复消费
 *
 * @author LiuBing
 * @date 2021/9/22
 */
public class RepetitionConsumerSample {

    public static void main(String[] args) {
        // 环形数组长度，必须是2的整数倍
        int ringBufferSize = 1024;
        // 创建事件（Event）对象的工厂
        MyEventFactory<String> eventFactory = new MyEventFactory<>();
        // 创建消费者线程工厂
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        // 队列满时等待策略
        WaitStrategy waitStrategy = new SleepingWaitStrategy();
        Disruptor<MyEvent<String>> disruptor =
                new Disruptor<>(eventFactory, ringBufferSize, threadFactory, SINGLE, waitStrategy);


        // 这里指定了2个消费者，那就会产生2个消费线程，一个事件会被消费2次
        EventHandler<MyEvent<String>> eventHandler = (event, sequence, endOfBatch) ->
                System.out.println(Thread.currentThread().getName() + "MyEventHandler消费消息：" + event.getData());
        EventHandler<MyEvent<String>> eventHandler2 = (event, sequence, endOfBatch) ->
                System.out.println(Thread.currentThread().getName() + "MyEventHandler——2消费消息：" + event.getData());
        disruptor.handleEventsWith(eventHandler, eventHandler2);
        // 分别指定异常处理器
        ExceptionHandler<MyEvent<String>> exceptionHandler = new MyExceptionHandler<>();
        disruptor.handleExceptionsFor(eventHandler).with(exceptionHandler);
        disruptor.handleExceptionsFor(eventHandler2).with(exceptionHandler);

        disruptor.start();

        for (int i = 0; i < 10; i++) {
            disruptor.publishEvent((event, sequence, param) -> event.setData(param), "One arg " + i);
        }

        disruptor.shutdown();
    }
}
