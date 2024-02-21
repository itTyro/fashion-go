package com.lzl.mapper;

import com.lzl.dto.GoodsSalesDTO;
import com.lzl.dto.OrderReportDTO;
import com.lzl.dto.OrdersPageQueryDTO;
import com.lzl.dto.TurnoverReportDTO;
import com.lzl.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     * @param order
     */
    void insert(Orders order);


    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber
     * @param userId
     */
    @Select("select * from orders where number = #{orderNumber} and user_id= #{userId}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 动态条件查询
     * @param ordersPageQueryDTO
     * @return
     */
    List<Orders> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);


    /**
     * 各个状态的订单数量统计
     *
     * @param status
     * @return
     */
    @Select("select count(*) from orders o where o.status = #{status};")
    Integer countStatus(Integer status);

    @Select("select * from orders where id = #{id} ;")
    Orders getById(Long id);

    @Select("select * from orders where status = #{status} and order_time < #{before15Time};")
    List<Orders> selectByStatusAndLtTime(Integer status, LocalDateTime before15Time);

    /**
     * 获取指定日期对应的营业额
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    List<TurnoverReportDTO> turnoverStatistics(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 获取订单数
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    List<OrderReportDTO> countOrderByDateAndStatus(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 获取销量前10
     */
    List<GoodsSalesDTO> getTop10(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 查询当天各状态订单数
     */
    Integer count(LocalDateTime beginTime,LocalDateTime endTime ,Integer status);
}
