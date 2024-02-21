package com.lzl.service;

import com.lzl.dto.EmployeeDTO;
import com.lzl.dto.EmployeeEditPwdDTO;
import com.lzl.dto.EmployeeLoginDTO;
import com.lzl.dto.EmployeePageQueryDTO;
import com.lzl.entity.Employee;
import com.lzl.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult getPage(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启用/禁用
     * @param status
     * @param id
     */
    void status(Integer status, Long id);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    void updateEmp(EmployeeDTO employeeDTO);

    /**
     * 修改密码
     * @param employeeEditPwdDTO
     */
    void editPassword(EmployeeEditPwdDTO employeeEditPwdDTO);
}
