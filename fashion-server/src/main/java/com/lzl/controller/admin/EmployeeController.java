package com.lzl.controller.admin;

import com.lzl.constant.JwtClaimsConstant;
import com.lzl.dto.EmployeeDTO;
import com.lzl.dto.EmployeeEditPwdDTO;
import com.lzl.dto.EmployeeLoginDTO;
import com.lzl.dto.EmployeePageQueryDTO;
import com.lzl.entity.Employee;
import com.lzl.properties.JwtProperties;
import com.lzl.result.PageResult;
import com.lzl.result.Result;
import com.lzl.service.EmployeeService;
import com.lzl.utils.JwtUtil;
import com.lzl.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation("员工登录")
    @PostMapping("/login")
   public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
       log.info("接收到的参数为：{}", employeeLoginDTO);

       Employee employee = employeeService.login(employeeLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());

       String jwt = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);

       EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
               .id(employee.getId())
               .name(employee.getName())
               .userName(employee.getUsername())
               .token(jwt).build();

       return Result.success(employeeLoginVO);
   }


    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @ApiOperation("新增员工")
   @PostMapping
   public Result save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工：{}", employeeDTO);
        if (ObjectUtils.isEmpty(employeeDTO)) {
            return Result.error("添加信息不能为空");
        }

        employeeService.save(employeeDTO);
        return Result.success();
   }

    /**
     * 动态分页查询
     * @param employeePageQueryDTO
     * @return
     */
   @ApiOperation("分页查询")
   @GetMapping("/page")
   public Result<PageResult> getPage(EmployeePageQueryDTO employeePageQueryDTO) {
       log.info("分页查询：{}", employeePageQueryDTO);
       PageResult pageResult = employeeService.getPage(employeePageQueryDTO);

       return Result.success(pageResult);
   }


    /**
     * 更改账号启用和禁用
     * @param status
     * @param id
     * @return
     */
   @ApiOperation("修改状态")
   @PostMapping("/status/{status}")
   public Result status(@PathVariable Integer status,  Long id) {
        log.info("启用和禁用员工：{}, {}", status, id);
        if (id == null) {
            return Result.error("id不能为空");
        }

        employeeService.status(status, id);
        return Result.success();
   }


    /**
     * 根据id查询员工信息
     *
     */
    @ApiOperation("根据id查询员工信息")
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息：{}", id);

        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    @ApiOperation("编辑员工")
    @PutMapping
    public Result updateEmp(@RequestBody EmployeeDTO employeeDTO) {
        log.info("编辑员工信息：{}", employeeDTO);
        employeeService.updateEmp(employeeDTO);
        return Result.success();
    }

    @ApiOperation("修改密码")
    @PutMapping("/editPassword")
    public Result editPassword(@RequestBody EmployeeEditPwdDTO employeeEditPwdDTO) {
        log.info("修改密码：{}", employeeEditPwdDTO);
        employeeService.editPassword(employeeEditPwdDTO);
        return Result.success();
    }


    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

}
