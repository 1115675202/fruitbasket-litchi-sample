package cn.fruitbasket.litchi.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static com.lmax.disruptor.dsl.ProducerType.SINGLE;

/**
 * 单消费者
 *
 * @author LiuBing
 * @date 2021/9/22
 */
public class SingleConsumerSample {

    public static void main(String[] args) {
        // 环形数组长度，必须是2的整数倍
        int ringBufferSize = 1024;
        // 创建事件（Event）对象的工厂
        MyEventFactory<String> eventFactory = new MyEventFactory<>();
        // 创建消费者线程工厂
        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
        // 队列满时等待策略
        WaitStrategy waitStrategy = new SleepingWaitStrategy();
        Disruptor<MyEvent<String>> disruptor =
                new Disruptor<>(eventFactory, ringBufferSize, threadFactory, SINGLE, waitStrategy);

        // 指定一个处理器
        MyEventHandler<String> eventHandler = new MyEventHandler<>();
        disruptor.handleEventsWith(eventHandler);
        // 处理器异常处理器
        ExceptionHandler<MyEvent<String>> exceptionHandler = new MyExceptionHandler<>();
        disruptor.setDefaultExceptionHandler(exceptionHandler);

        disruptor.start();

        // 通过事件转换器（EventTranslator）来指明如何将发布的数据转换到事件对象（Event）中
        // 这里是一个参数的转换器，另外还有两个（EventTranslatorTwoArg）、三个（EventTranslatorThreeArg）
        // 和多个（EventTranslatorVararg）参数的转换器可以使用，参数类型可以不一样
        EventTranslatorOneArg<MyEvent<String>, String> eventTranslatorOneArg =
                new EventTranslatorOneArg<MyEvent<String>, String>() {
                    @Override
                    public void translateTo(MyEvent<String> event, long sequence, String arg0) {
                        event.setData(arg0);
                    }
                };

        // 发布消息
        for (int i = 0; i < 10; i++) {
            disruptor.publishEvent(eventTranslatorOneArg, "One arg " + i);
        }

        disruptor.shutdown();
    }
}
