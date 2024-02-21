package com.lzl.service;

import com.lzl.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void save(AddressBook addressBook);

    List<AddressBook> list();

    AddressBook defaultAddress();

    // 设置默认地址
    void setDefault(Long id);

    AddressBook getById(Long id);

    // 修改地址信息
    void update(AddressBook addressBook);

    void deleteById(Long id);
}
