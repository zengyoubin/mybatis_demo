package mybatis.spring_mybatis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ZYB
 * @since 2016-12-23 下午2:09
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
@Import(EntityScanRegistrar.class)
public @interface EntityScan {
    String[] basePackages() default {};
}
