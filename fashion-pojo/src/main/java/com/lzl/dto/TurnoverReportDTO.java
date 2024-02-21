package com.lzl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TurnoverReportDTO implements Serializable {

    private String orderDate; // 订单日期
    private BigDecimal orderMoney; // 订单营业额
}
