package mybatis.mapper;

import org.apache.ibatis.jdbc.SQL;

/**
 * @author ZYB
 * @since 2016-12-22 下午3:42
 */
public class UserProvider {
    public  String getUser(){
        return new SQL() {{
            SELECT("*").FROM("user");
        }}.toString();
    }
}
