package com.lzl.service.impl;

import com.lzl.dto.GoodsSalesDTO;
import com.lzl.dto.OrderReportDTO;
import com.lzl.dto.TurnoverReportDTO;
import com.lzl.dto.UserReportDTO;
import com.lzl.entity.Orders;
import com.lzl.mapper.OrderMapper;
import com.lzl.mapper.UserMapper;
import com.lzl.service.ReportService;
import com.lzl.service.WorkspaceService;
import com.lzl.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        // 1. 获取时间列表
        List<String> dateList = getDateList(begin, end);

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 2. 获取营业额
        List<TurnoverReportDTO> turnoverReportDTOList = orderMapper.turnoverStatistics(beginTime, endTime, Orders.COMPLETED);

        // 2.1 将List转成Map，然后遍历时间列表，根据时间获取value，没有值就存入0
        Map<String, BigDecimal> collect = turnoverReportDTOList.stream().collect(Collectors.toMap(TurnoverReportDTO::getOrderDate, TurnoverReportDTO::getOrderMoney));

        List<BigDecimal> amoutList = dateList.stream().map(date ->
                collect.get(date) == null ? new BigDecimal(0) : collect.get(date)
        ).collect(Collectors.toList());

        // 3. 封装数据返回
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(amoutList, ","))
                .build();

        return turnoverReportVO;
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {

        // 1. 获取时间列表
        List<String> dateList = getDateList(begin, end);

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 2. 查询新增用户数量
        List<UserReportDTO> userReportDTOList = userMapper.userCountByDate(beginTime, endTime);

        // 2.1 转为map
        Map<String, Integer> userReportMap = userReportDTOList.stream().collect(Collectors.toMap(UserReportDTO::getCreateUserTime, UserReportDTO::getUserCount));
        // 2.2 遍历map获取数据集合
        List<Integer> newUserList = dateList.stream().map(date -> userReportMap.get(date) == null ? 0 : userReportMap.get(date)).collect(Collectors.toList());

        // 3. 查询每天的用户总量 查询前一天的用户基数，然后根据每天新增去累加，得到数据，性能较好，不然需要查询多次数据库，影响性能
        Integer count = userMapper.getBaseCount(beginTime);

        List<Integer> totalUserList = new ArrayList<>();
        for (Integer add : newUserList) {
            count += add;
            totalUserList.add(count);
        }


        return new UserReportVO(StringUtils.join(dateList, ","), StringUtils.join(totalUserList, ","), StringUtils.join(newUserList, ","));
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        // 1. 获取时间列表
        List<String> dateList = getDateList(begin, end);

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 2. 获取每天订单数
        List<OrderReportDTO> countList = orderMapper.countOrderByDateAndStatus(beginTime, endTime, null);
        // 2.1 转成map
        Map<String, Integer> orderCountMap = countList.stream().collect(Collectors.toMap(OrderReportDTO::getOrderDate, OrderReportDTO::getOrderCount));
        // 2.2 遍历获取每一天的订单数
        List<Integer> orderCountList = dateList.stream().map(date -> orderCountMap.get(date) == null ? 0 : orderCountMap.get(date)).collect(Collectors.toList());


        // 3. 获取每天有效订单数
        List<OrderReportDTO> validList = orderMapper.countOrderByDateAndStatus(beginTime, endTime, Orders.COMPLETED);
        Map<String, Integer> validMap = validList.stream().collect(Collectors.toMap(OrderReportDTO::getOrderDate, OrderReportDTO::getOrderCount));
        // 2.2 遍历获取每一天的订单数
        List<Integer> validOrderList = dateList.stream().map(date -> validMap.get(date) == null ? 0 : validMap.get(date)).collect(Collectors.toList());

        // 4. 获取所有订单数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();


        // 5. 获取所有有效订单数
        Integer validOrderCount = validOrderList.stream().reduce(Integer::sum).get();

        // 6. 订单有效率
        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        return new OrderReportVO(StringUtils.join(dateList, ","), StringUtils.join(orderCountList, ","),
                StringUtils.join(validOrderList, ","), totalOrderCount, validOrderCount, orderCompletionRate);
    }

    /**
     * 销量前10
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> Top10List = orderMapper.getTop10(beginTime, endTime, Orders.COMPLETED);
        List<String> nameList = Top10List.stream().map(GoodsSalesDTO::getDishName).collect(Collectors.toList());
        List<Integer> numberList = Top10List.stream().map(GoodsSalesDTO::getCount).collect(Collectors.toList());
        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
        return salesTop10ReportVO;
    }


    /**
     * 获取时间列表
     *
     * @param begin
     * @param end
     * @return
     */
    private static List<String> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> localDateList = begin.datesUntil(end.plusDays(1)).collect(Collectors.toList());
        return localDateList.stream().map(localDate -> localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).collect(Collectors.toList());
    }


    /**
     * 导出近30天的运营数据报表
     *
     * @param response
     **/
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        //查询概览运营数据，提供给Excel模板文件
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            //基于提供好的模板文件创建一个新的Excel表格对象
            Workbook excel = new XSSFWorkbook(inputStream);
            //获得Excel文件中的一个Sheet页
            Sheet sheet = excel.getSheet("Sheet1");

            sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);
            //获得第4行
            Row row = sheet.getRow(3);
            //获取单元格
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                //准备明细数据
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            //通过输出流将文件下载到客户端浏览器中
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.flush();
            out.close();
            excel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
