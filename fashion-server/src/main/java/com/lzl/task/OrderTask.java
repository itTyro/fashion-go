package com.lzl.task;

import com.lzl.entity.Orders;
import com.lzl.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单定时更新状态
 */
@Slf4j
@Component
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0/30 * * * * ?") // 每隔30S执行一次
    public void cancelOrder() {
        log.info("执行超时未支付取消订单任务");

        // 1. 查询符合条件,待支付，且为15分钟之前下的单
        LocalDateTime before15Time = LocalDateTime.now().minusMinutes(15);
        List<Orders> ordersList = orderMapper.selectByStatusAndLtTime(1, before15Time);

        // 2. 取消订单
        if (!CollectionUtils.isEmpty(ordersList)) {
            ordersList.forEach(orders -> {
                log.info("执行定时取消任务操作：{}", orders.getId());
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orders.setCancelReason("支付超时，系统自动取消");
                orderMapper.update(orders);
            });
        }
    }


    @Scheduled(cron = "* * 1 * * ?") // 每天的1点执行
    public void completeOrder() {

        log.info("执行将派送中改为完成");

        // 1. 查询符合条件,派送中，且为2小时之前下的单
        LocalDateTime before2HourTime = LocalDateTime.now().minusHours(2);
        List<Orders> ordersList = orderMapper.selectByStatusAndLtTime(Orders.DELIVERY_IN_PROGRESS, before2HourTime);

        // 2. 完成订单
        if (!CollectionUtils.isEmpty(ordersList)) {
            ordersList.forEach(orders -> {
                log.info("执行定时完成任务操作：{}", orders.getId());
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(LocalDateTime.now());
                orderMapper.update(orders);
            });
        }
    }
}
