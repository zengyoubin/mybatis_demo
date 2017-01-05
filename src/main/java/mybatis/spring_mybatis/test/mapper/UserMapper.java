package mybatis.spring_mybatis.test.mapper;

import mybatis.bean.PageList;
import mybatis.bean.User;
import mybatis.spring_mybatis.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ZYB
 * @since 2016-12-27 下午4:20
 */
public interface UserMapper extends Mapper<User> {

    @Select("select * from user where name=#{name}")
    List<User> selectPageList(PageList<User> pageList, String name);

    @Select("select sex from user")
    List<User.Sex> selectSex();

    @Insert("<script>" +
        "insert into user (name,age)" +
        " values " +
        "<foreach collection='list' item='item' separator=','>" +
        " (#{item.name},#{item.age})" +
        "</foreach>" +
        "</script>")
    int insertBatch(List<User> users);
}
