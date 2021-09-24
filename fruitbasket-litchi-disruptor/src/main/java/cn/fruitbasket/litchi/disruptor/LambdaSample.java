package cn.fruitbasket.litchi.disruptor;


import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

import static com.lmax.disruptor.dsl.ProducerType.SINGLE;

/**
 * @author LiuBing
 * @date 2021/9/22
 */
public class LambdaSample {


    public static void main(String[] args) {
        // 环形数组长度，必须是2的整数倍
        int ringBufferSize = 2;
        // 创建消费者线程工厂
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        // 队列满时等待策略
        WaitStrategy waitStrategy = new SleepingWaitStrategy();
        Disruptor<MyEvent<String>> disruptor =
                new Disruptor<>(MyEvent::new, ringBufferSize, threadFactory, SINGLE, waitStrategy);

        // 指定一个处理器
        EventHandler<MyEvent<String>> eventHandler = (event, sequence, endOfBatch) -> {
            System.out.println(Thread.currentThread().getName() + "MyEventHandler消费消息：" + event.getData());
            Thread.sleep(1000000);
        };
        disruptor.handleEventsWith(eventHandler);
        // 处理器异常处理器
        ExceptionHandler<MyEvent<String>> exceptionHandler = new MyExceptionHandler<>();
        disruptor.setDefaultExceptionHandler(exceptionHandler);

        disruptor.start();

        // 通过事件转换器（EventTranslator）来指明如何将发布的数据转换到事件对象（Event）中
        // 一个参数的转换器
        disruptor.publishEvent((event, sequence, param) -> {
            event.setData(param);
            System.out.println("发布");
        }, "One arg ");
        // 两个参数的转换器
        disruptor.publishEvent((event, sequence, pA, pB) -> {
            event.setData(pA + pB);
            System.out.println("发布");
        }, "Two arg ", 1);
        // 三个参数的转换器
        disruptor.publishEvent((event, sequence, pA, pB, pC) -> {
                    event.setData(pA + pB + pC);
                    System.out.println("发布");
                }
                , "Three arg ", 1, false);
        // 多个参数的转换器
        disruptor.getRingBuffer().publishEvent((event, sequence, params) -> {
            List<String> paramList = Arrays.stream(params).map(Object::toString).collect(Collectors.toList());
            event.setData("Var arg " + String.join(",", paramList));
        }, "param1", "param2", "param3");

        disruptor.shutdown();
    }
}
