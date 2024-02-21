package com.lzl.service;

import com.lzl.dto.*;
import com.lzl.result.PageResult;
import com.lzl.vo.OrderPaymentVO;
import com.lzl.vo.OrderStatisticsVO;
import com.lzl.vo.OrderSubmitVO;
import com.lzl.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);


    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo) throws Exception;

    /**
     * 管理端订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    /**
     * 完成订单
     * @param id
     */
    void complete(Long id);

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 派送订单
     * @param id
     */
    void delivery(Long id);

    /**
     * 查看订单详情
     * @param id
     * @return
     */
    OrderVO details(Long id);

    /**
     * 用户端查看历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 用户端再来一单
     * @param id
     */
    void repetition(Long id);

    /**
     * 用户端取消订单
     * @param id
     */
    void userCancelById(Long id);

    /**
     * 用户催单
     * @param id
     */
    void reminder(Long id) throws Exception;
}
