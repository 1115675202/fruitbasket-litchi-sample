package cn.fruitbasket.litchi.disruptor;

/**
 * 事件
 *
 * @param <T>发布的数据类型
 * @author LiuBing
 * @date 2021/9/22
 */
public class MyEvent<T> {

    private T data;

    public T getData() {
        return data;
    }

    public MyEvent<T> setData(T data) {
        this.data = data;
        return this;
    }
}
