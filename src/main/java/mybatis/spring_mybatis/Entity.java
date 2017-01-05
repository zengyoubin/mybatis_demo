package mybatis.spring_mybatis;

import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ZYB
 * @since 2016-12-23 上午10:25
 */
@Data
public class Entity {
    private String tableName;
    private String columns;
    private Map<String, String> properties;
    private Class<?> entityClass;
    private String primaryKeyName;
    private String primaryKeyColumn;

    public void setProperties(Map<String, String> properties){
        this.properties = properties;
        this.columns = properties.values().stream().collect(Collectors.joining(","));
    }
}
