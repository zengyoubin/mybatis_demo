package mybatis.spring_mybatis;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

/**
 * @author ZYB
 * @since 2016-12-23 上午10:16
 */
public interface Mapper<T> {

    @InsertProvider(type = MapperProvider.class, method = "insert")
    int insert(T t);

    @UpdateProvider(type = MapperProvider.class, method = "update")
    int update(T t);

    @ResultMap("ResultMap")
    @SelectProvider(type = MapperProvider.class, method = "selectByPrimerKey")
    T selectByPrimerKey(T t);

    @ResultMap("ResultMap")
    @SelectProvider(type = MapperProvider.class, method = "select")
    List<T> select(T t);

    @InsertProvider(type = MapperProvider.class, method = "insertList")
    int insertList(@Param("lists") List<T> lists);
}
