package com.lzl.controller.user;

import com.lzl.entity.AddressBook;
import com.lzl.result.Result;
import com.lzl.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api("地址簿管理")
@RestController
@RequestMapping("/user/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @ApiOperation("新增地址")
    @PostMapping
    public Result save(@RequestBody AddressBook addressBook) {
        log.info("新增地址：{}", addressBook);
        addressBookService.save(addressBook);
        return Result.success();
    }

    @ApiOperation("查询地址列表")
    @GetMapping("/list")
    public Result<List<AddressBook>> list() {
        log.info("查询所有地址列表");
        List<AddressBook> addressBookList = addressBookService.list();
        return Result.success(addressBookList);
    }

    @ApiOperation("查询默认地址")
    @GetMapping("/default")
    public Result<AddressBook> defaultAddress() {
        log.info("查询默认地址");
        AddressBook addressBook = addressBookService.defaultAddress();
        return Result.success(addressBook);
    }

    @ApiOperation("设置默认地址")
    @PutMapping("/default")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        Long id = addressBook.getId();
        log.info("设置默认地址：{}", id);
        addressBookService.setDefault(id);
        return Result.success();
    }


    @ApiOperation("根据id查询地址")
    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Long id) {
        log.info("根据id查询地址：{}", id);
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }


    @ApiOperation("根据id修改地址")
    @PutMapping
    public Result updateAddress(@RequestBody AddressBook addressBook) {
        log.info("根据id修改地址：{}", addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    @ApiOperation("删除地址")
    @DeleteMapping
    public Result deleteById(Long id) {
        log.info("根据id删除地址：{}", id);
        addressBookService.deleteById(id);
        return Result.success();
    }
}
