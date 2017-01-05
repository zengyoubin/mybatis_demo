package mybatis.spring_mybatis.test;

import mybatis.bean.PageList;
import mybatis.bean.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import mybatis.spring_mybatis.test.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZYB
 * @since 2016-12-27 下午4:33
 */
@ContextConfiguration(classes = Config.class)
public class MapperTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void insert() {
        User user = new User();
        user.setAge(23);
        user.setName("小yao");
        user.setSex(User.Sex.MAN);
        int insert = userMapper.insert(user);
        Assert.assertEquals(1, insert);
    }

    @Test
    public void selectByPrimerKey() {
        User user = userMapper.selectByPrimerKey(new User(11));
        Assert.assertNotNull(user);
    }

    @Test
    public void select() {
        User entity = new User();
        entity.setName("小明");
        List<User> list = userMapper.select(entity);
        Assert.assertEquals(6, list.size());
    }

    @Test
    public void update() {
        User user = new User();
        user.setId(3);
        user.setName("小金");
        int update = userMapper.update(user);
        Assert.assertEquals(1, update);
    }

    @Test
    public void selectPageList() {
        PageList<User> pageList = new PageList<>(1, 2);
        userMapper.selectPageList(pageList, "小明");
        Assert.assertEquals(2, pageList.getList().size());
    }

    @Test
    public void selectSexTest() {
        List<User.Sex> sexes = userMapper.selectSex();
        Assert.assertEquals(9, sexes.size());
    }

    @Test
    public void insertBatch(){
        List<User> users = new ArrayList<>();
        for(int i=0;i<4;i++) {
            User user = new User();
            // user.setAge(1+i);
            user.setName("name11"+i);
            users.add(user);
        }
        int insertBatch = userMapper.insertBatch(users);
        System.out.println(insertBatch);


    }
}
