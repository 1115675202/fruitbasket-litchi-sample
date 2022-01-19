package cn.fruitbasket.litchi.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * 创建事件的工厂
 *
 * @param <T>发布的数据类型
 * @author LiuBing
 * @since 2021/9/22
 */
public class MyEventFactory<T> implements EventFactory<MyEvent<T>> {

    @Override
    public MyEvent<T> newInstance() {
        return new MyEvent<>();
    }
}
