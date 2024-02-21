package com.lzl.mapper;

import com.lzl.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    @Insert("insert into address_book (user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default) " +
            "values" +
            " (#{userId}, #{consignee}, #{sex}, #{phone}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault});")
    void save(AddressBook addressBook);

    List<AddressBook> list(AddressBook addressBook);

    /**
     * 动态修改数据
     *
     * @param addressBook
     */
    void update(AddressBook addressBook);

    @Select("select * from address_book where id = #{id} ;")
    AddressBook getById(Long id);

    @Delete("delete from address_book where id = #{id};")
    void deleteById(Long id);
}
