package com.lzl.service.impl;

import com.lzl.context.BaseContext;
import com.lzl.entity.AddressBook;
import com.lzl.mapper.AddressBookMapper;
import com.lzl.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;
    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);

        addressBookMapper.save(addressBook);
    }

    /**
     * 查询当前用户所有地址
     * @return
     */
    @Override
    public List<AddressBook> list() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(userId);

        return addressBookMapper.list(addressBook);
    }

    /**
     * 查询默认地址
     * @return
     */
    @Override
    public AddressBook defaultAddress() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = AddressBook.builder()
                .userId(userId)
                .isDefault(1)
                .build();
        List<AddressBook> list = addressBookMapper.list(addressBook);

        if (!CollectionUtils.isEmpty(list)) {
            addressBook = list.get(0);


            return addressBook;

        }
        return null;
    }

    /**
     * 设置默认地址
     * @param id
     */
    @Override
    public void setDefault(Long id) {
        // 查询是否已经存在默认地址
        AddressBook addressBook = this.defaultAddress();

        if (!ObjectUtils.isEmpty(addressBook)) {
            // 将已经存在的默认地址改成非默认地址
            addressBook.setIsDefault(0);
            addressBookMapper.update(addressBook);
        }

        AddressBook address = AddressBook.builder()
                .id(id)
                .isDefault(1)
                .build();

        // 设置为默认地址
        addressBookMapper.update(address);
    }

    @Override
    public AddressBook getById(Long id) {

        return addressBookMapper.getById(id);
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }


}
