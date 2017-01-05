package mybatis.spring_mybatis;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZYB
 * @since 2016-12-23 上午10:24
 */
@Data
public class EntityHelp {
    private Map<Class<?>, Entity> map = new HashMap<>();
    private static EntityHelp entityHelp = new EntityHelp();
    private Map<Class<?>, Class<?>> entityMapper;

    private EntityHelp() {
    }

    public static EntityHelp newInstance() {
        return entityHelp;
    }

    public Entity getEntity(Class clazz) {
        return map.get(clazz);
    }

    public void addEntity(Class clazz, Entity entity) {
        map.put(clazz, entity);
    }
}
