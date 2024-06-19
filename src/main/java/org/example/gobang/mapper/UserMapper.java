package org.example.gobang.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.gobang.model.User;

@Mapper
public interface UserMapper {
    @Insert("insert into user (user_id, username,password,score,total_count,win_count) values (null,#{username},#{password},1000,0,0)")
    public Integer insertUser(String username, String password);

    @Select("select user_id, username, password, score, total_count, win_count from user where username=#{username}")
    public User selectByUsername(String username);
}
