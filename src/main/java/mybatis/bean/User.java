package mybatis.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

/**
 * @author ZYB
 * @since 2016-12-22 下午2:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private Integer id;
    private String name;
    private Integer age;
    private Sex sex;

    public enum Sex {
        NO,
        MAN,
        WOMAN
    }

    public User(int id) {
        this.id = id;
    }

}
