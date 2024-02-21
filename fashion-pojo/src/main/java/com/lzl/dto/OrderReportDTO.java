package com.lzl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderReportDTO implements Serializable {
    private String orderDate; // 订单日期
    private Integer orderCount; //订单数量
}
