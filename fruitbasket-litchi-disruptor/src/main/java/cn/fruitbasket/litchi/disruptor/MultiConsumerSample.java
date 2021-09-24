package cn.fruitbasket.litchi.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static com.lmax.disruptor.dsl.ProducerType.SINGLE;

/**
 * @author LiuBing
 * @date 2021/9/22
 */
public class MultiConsumerSample {

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

        // 处理器异常处理器
        ExceptionHandler<MyEvent<String>> exceptionHandler = new MyExceptionHandler<>();
        disruptor.setDefaultExceptionHandler(exceptionHandler);

        // 设置2个消费者，2个线程，一条消息只被一个消费者消费
        WorkHandler<MyEvent<String>> workHandler = tMyEvent ->
                System.out.println(Thread.currentThread().getName() + "WorkHandler消费消息：" + tMyEvent.getData());
        disruptor.handleEventsWithWorkerPool(workHandler, workHandler);

        disruptor.start();

        for (int i = 0; i < 10; i++) {
            disruptor.publishEvent((event, sequence, param) -> event.setData(param), "One arg " + i);
        }

        disruptor.shutdown();
    }
}
