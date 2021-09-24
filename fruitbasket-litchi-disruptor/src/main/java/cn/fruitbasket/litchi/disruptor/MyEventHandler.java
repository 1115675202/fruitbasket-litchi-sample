package cn.fruitbasket.litchi.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * 事件消费方法
 *
 * @param <T>发布的数据类型
 * @author LiuBing
 * @date 2021/9/22
 */
public class MyEventHandler<T> implements EventHandler<MyEvent<T>> {

    @Override
    public void onEvent(MyEvent<T> tMyEvent, long l, boolean b) throws Exception {
        System.out.println(Thread.currentThread().getName() + "MyEventHandler消费消息：" + tMyEvent.getData());
    }
}
