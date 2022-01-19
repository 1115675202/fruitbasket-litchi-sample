package cn.fruitbasket.litchi.disruptor;

import com.lmax.disruptor.ExceptionHandler;

/**
 * 消费者异常处理器
 *
 * @param <T>发布的数据类型
 * @author LiuBing
 * @since 2021/9/22
 */
public class MyExceptionHandler<T> implements ExceptionHandler<MyEvent<T>> {

    @Override
    public void handleEventException(Throwable ex, long sequence, MyEvent<T> event) {
        System.out.println("handleEventException");
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        System.out.println("handleOnStartException");
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        System.out.println("handleOnShutdownException");
    }
}
