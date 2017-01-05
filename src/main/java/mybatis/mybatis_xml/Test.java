package mybatis.mybatis_xml;

import mybatis.bean.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;

import java.io.InputStream;
import java.util.List;

/**
 * @author ZYB
 * @since 2016-12-28 下午12:49
 */
public class Test {
    public static void main(String[] args) throws Exception {
        InputStream resourceAsStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

       try( SqlSession sqlSession = sqlSessionFactory.openSession()) {
           List<User> list = sqlSession.selectList("mybatis.mybatis_xml.UserMapper.getAll");
           Assert.assertEquals(8, list.size());
       }

    }
}
