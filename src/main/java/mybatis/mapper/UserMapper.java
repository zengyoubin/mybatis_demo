package mybatis.mapper;

import mybatis.bean.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author ZYB
 * @since 2016-12-22 下午3:09
 */
public interface UserMapper {

    @Insert("insert into user(name,age,sex) values(#{name},#{age},#{sex}) ")
    void add(User user);

    @SelectProvider(type = UserProvider.class, method = "getUser")
    List<User> getUser();
}
