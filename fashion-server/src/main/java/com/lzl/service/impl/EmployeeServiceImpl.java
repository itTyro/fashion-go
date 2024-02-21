package com.lzl.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lzl.constant.MessageConstant;
import com.lzl.constant.StatusConstant;
import com.lzl.context.BaseContext;
import com.lzl.dto.EmployeeDTO;
import com.lzl.dto.EmployeeEditPwdDTO;
import com.lzl.dto.EmployeeLoginDTO;
import com.lzl.dto.EmployeePageQueryDTO;
import com.lzl.entity.Employee;
import com.lzl.exception.AccountLockedException;
import com.lzl.exception.AccountNotFoundException;
import com.lzl.exception.PasswordErrorException;
import com.lzl.mapper.EmployeeMapper;
import com.lzl.result.PageResult;
import com.lzl.service.EmployeeService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.lzl.constant.MessageConstant.ACCOUNT_NOT_FOUND;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final static String PASSWORD_ERROR_KEY = "login:password:error:";
    private final static String PASSWORD_LOCK_KEY = "login:lock:";

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {

        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();
        final String passwordErrorKey = PASSWORD_ERROR_KEY + username + ":";

        // 判断用户密码有没有错误超过五次,是否被锁定
        Long expireTime = stringRedisTemplate.getExpire(PASSWORD_LOCK_KEY + username, TimeUnit.MINUTES);
        if (expireTime != null && expireTime > 0) {
            throw new PasswordErrorException("您的账号密码输入错误超过五次，请" + expireTime + "分钟后再试");
        }


        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (Objects.isNull(employee)) {
            //账号不存在
            throw new AccountNotFoundException(ACCOUNT_NOT_FOUND);
        }

        password = DigestUtils.md5DigestAsHex(password.getBytes());


        // 密码比对
        if (!employee.getPassword().equals(password)) {
            // 密码错误
            stringRedisTemplate.opsForValue().set(passwordErrorKey + RandomStringUtils.randomAlphabetic(5), "passwordError", 5, TimeUnit.MINUTES);

            // 判断用户输入密码错误几次
            Set<String> count = stringRedisTemplate.keys(passwordErrorKey + "*");
            if (!ObjectUtils.isEmpty(count) && count.size() >= 5) {
                stringRedisTemplate.opsForValue().set(PASSWORD_LOCK_KEY + username, "loginLock", 1, TimeUnit.HOURS);
                throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR_FIVE);
            }

            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }


        //3、返回实体对象
        return employee;
    }


    /**
     * 新增员工操作
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setStatus(StatusConstant.ENABLE);

        employeeMapper.save(employee);

    }


    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult getPage(EmployeePageQueryDTO employeePageQueryDTO) {
        // 设置分页参数
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        List<Employee> pageList = employeeMapper.getPage(employeePageQueryDTO.getName());
        Page<Employee> page = (Page<Employee>) pageList;

        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     * 修改员工状态
     * @param status
     * @param id
     */
    @Override
    public void status(Integer status, Long id) {
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .build();

        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("*****");
        return employee;
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    @Override
    public void updateEmp(EmployeeDTO employeeDTO) {
        Employee employee = Employee.builder()
                .build();
        BeanUtils.copyProperties(employeeDTO, employee);
        employeeMapper.update(employee);
    }

    @Transactional
    @Override
    public void editPassword(EmployeeEditPwdDTO employeeEditPwdDTO) {
        String oldPassword = employeeEditPwdDTO.getOldPassword();
        String newPassword = employeeEditPwdDTO.getNewPassword();
        // 加密
        oldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        // 查询
        Long empId = BaseContext.getCurrentId();
        Employee employee = employeeMapper.getById(empId);
        if (!employee.getPassword().equals(oldPassword)) {
            throw new PasswordErrorException("原始密码错误");
        }
        // 加密新密码
        newPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        employee.setPassword(newPassword);
        employeeMapper.update(employee);

    }


}
