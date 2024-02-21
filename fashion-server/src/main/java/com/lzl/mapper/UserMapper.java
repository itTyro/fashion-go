package com.lzl.mapper;

import com.lzl.dto.UserReportDTO;
import com.lzl.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid};")
    User selectByOpenid(String openid);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into user (openid, name, phone, sex, id_number, avatar, create_time)" +
            " values " +
            "(#{openid}, #{name},#{phone},#{sex},#{idNumber},#{avatar},#{createTime});")
    void save(User user);


    @Select("select * from user where id = #{id} ;")
    User getById(Long userId);

    /**
     * 查询每天新增的用户数
     * @param beginTime
     * @param endTime
     * @return
     */
    List<UserReportDTO> userCountByDate(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 查询某段时间前一天的用户数量
     * @param beginTime
     * @return
     */
    @Select("select count(*) from user  where create_time < #{beginTime};")
    Integer getBaseCount(LocalDateTime beginTime);
}
