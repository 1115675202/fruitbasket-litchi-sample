package cn.fruitbasket.litchi.shrdingjdbc.controller;


import cn.fruitbasket.litchi.shrdingjdbc.entity.TOrder;
import cn.fruitbasket.litchi.shrdingjdbc.service.ITOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

import static java.util.stream.Collectors.toList;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author LiuBing
 * @since 2021-11-26
 */
@RestController
@RequestMapping("order")
public class TOrderController {

    @Autowired
    private ITOrderService itOrderService;

    @GetMapping("add")
    public String add() {
        TOrder order = new TOrder()
                .setUserId(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE))
                .setDescription("测试订单");
        itOrderService.save(order);
        return order.toString();
    }

    @GetMapping("list")
    public String list() {
        return itOrderService.list().stream()
                .map(tOrder -> tOrder.toString() + "<br>").collect(toList()).toString();
    }
}
