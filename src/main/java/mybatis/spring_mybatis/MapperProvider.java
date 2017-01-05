package mybatis.spring_mybatis;

import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author ZYB
 * @since 2016-12-23 上午10:18
 */
@Component
public class MapperProvider<T> {
    @Autowired
    private Configuration configuration;
    protected Entity entity;

    public MapperProvider() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            Type type = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
            this.entity = EntityHelp.newInstance().getEntity((Class<?>) type);
        }
    }

    public String insert(T t) {
        Entity entity = EntityHelp.newInstance().getEntity(t.getClass());
        return new SQL() {{
            INSERT_INTO(entity.getTableName());
            try {
                PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(t.getClass());
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    String name = propertyDescriptor.getName();
                    if ("class".equals(name)) continue;
                    Object value = propertyDescriptor.getReadMethod().invoke(t);
                    if (value == null) continue;
                    String column = entity.getProperties().get(name);
                    VALUES(column, "#{" + name + "}");
                }

            } catch (Exception e) {
                throw new RuntimeException("insert is exception.");
            }
        }}.toString();
    }

    public String update(T t) {
        Entity entity = EntityHelp.newInstance().getEntity(t.getClass());
        return new SQL() {{
            UPDATE(entity.getTableName());
            try {
                PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(t.getClass());
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    String name = propertyDescriptor.getName();
                    if ("class".equals(name)) continue;
                    Object value = propertyDescriptor.getReadMethod().invoke(t);
                    if (value == null) continue;
                    String column = entity.getProperties().get(name);
                    SET(column + "=" + "#{" + name + "}");
                }
                WHERE(entity.getPrimaryKeyColumn() + "=#{" + entity.getPrimaryKeyName() + "}");

            } catch (Exception e) {
                throw new RuntimeException("insert is exception.");
            }
        }}.toString();
    }

    public String selectByPrimerKey(T t) {
        Entity entity = EntityHelp.newInstance().getEntity(t.getClass());
        return new SQL() {{
            SELECT(entity.getColumns()).FROM(entity.getTableName());
            WHERE(entity.getPrimaryKeyColumn() + "=#{" + entity.getPrimaryKeyName() + "}");
        }}.toString();
    }

    public String select(T t) {
        Entity entity = EntityHelp.newInstance().getEntity(t.getClass());
        return new SQL() {{
            SELECT(entity.getColumns()).FROM(entity.getTableName());
            try {
                PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(t.getClass());
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    String name = propertyDescriptor.getName();
                    if ("class".equals(name)) continue;
                    Object value = propertyDescriptor.getReadMethod().invoke(t);
                    if (value == null) continue;
                    String column = entity.getProperties().get(name);
                    WHERE(column + "=" + "#{" + name + "}");
                }

            } catch (Exception e) {
                throw new RuntimeException("insert is exception.");
            }
        }}.toString();
    }

    // @Insert("<script>" +
    //     "insert into user (name,age)" +
    //     " values " +
    //     "<foreach collection='list' item='item' separator=','>" +
    //     " (#{item.name},#{item.age})" +
    //     "</foreach>" +
    //     "</script>")
    public String insertList(List<T> lists) {
        if (lists.size() == 0)
            return "";
        Entity entity = EntityHelp.newInstance().getEntity(lists.get(0).getClass());
        StringBuilder sb = new StringBuilder();
        sb.append("<script> insert into ")
            .append(entity.getTableName())
            .append(" (")
            .append(entity.getColumns())
            .append(")")
            .append(" values ")
            .append("<foreach collection='list' item='item' separator=','> (");

        for (String s : entity.getProperties().keySet()) {
            sb.append("#{item.").append(s).append("},");
        }

        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append(") </foreach></script>");
        XMLLanguageDriver xmlLanguageDriver = new XMLLanguageDriver();
        return sb.toString();

    }

}
