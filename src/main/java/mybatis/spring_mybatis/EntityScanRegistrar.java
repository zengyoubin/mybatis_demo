package mybatis.spring_mybatis;

import com.google.common.base.CaseFormat;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ZYB
 * @since 2016-12-23 下午2:15
 */
public class EntityScanRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        Configuration configuration = beanFactory.getBean(Configuration.class);
        List<String> mapperBasePackages = getBasePackages(annotationMetadata, MapperScan.class);
        Map<Class<?>, Class<?>> entityMapper = new HashMap<>();
        for (Class<?> mapperClass : getClasses(mapperBasePackages)) {
            if (mapperClass.getGenericInterfaces().length == 0) continue;
            ParameterizedType type = (ParameterizedType) mapperClass.getGenericInterfaces()[0];
            if (type.getRawType().equals(Mapper.class))
                entityMapper.put((Class<?>) type.getActualTypeArguments()[0], mapperClass);

        }
        EntityHelp.newInstance().setEntityMapper(entityMapper);

        List<String> entityBasePackages = getBasePackages(annotationMetadata, EntityScan.class);
        Set<Class<?>> entityClasses = getClasses(entityBasePackages);
        for (Class<?> entityClass : entityClasses) {
            Class<?> mapperClass = entityMapper.get(entityClass);
            if (mapperClass == null) continue;

            Entity entity = new Entity();
            entity.setTableName(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityClass.getSimpleName()));
            Map<String, String> properties = new HashMap<>();
            List<ResultMapping> resultMappingList = new ArrayList<>();
            for (Field field : entityClass.getDeclaredFields()) {
                String name = field.getName();
                String column = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
                properties.put(name, column);
                ResultMapping.Builder builder = new ResultMapping.Builder(configuration, name, column, field.getType());
                if (field.getAnnotation(Id.class) != null) {
                    builder.flags(Collections.singletonList(ResultFlag.ID));
                    entity.setPrimaryKeyColumn(column);
                    entity.setPrimaryKeyName(name);
                }
                if (field.getType().isEnum()) {
                    Class<?> type = field.getType();
                    builder.typeHandler(new EnumOrdinalTypeHandler(type));
                    configuration.getTypeHandlerRegistry().register(type,new EnumOrdinalTypeHandler(type));
                }
                resultMappingList.add(builder.build());
            }
            String resultMapId = mapperClass.getCanonicalName() + ".ResultMap";
            ResultMap resultMap = new ResultMap.Builder(configuration, resultMapId, entityClass, resultMappingList).build();
            configuration.addResultMap(resultMap);
            entity.setProperties(properties);
            EntityHelp.newInstance().addEntity(entityClass, entity);

        }
    }

    private List<String> getBasePackages(AnnotationMetadata annotationMetadata, Class<?> clazz) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(clazz.getName()));
        List<String> basePackages = new ArrayList<>();
        for (String packages : annotationAttributes.getStringArray("basePackages")) {
            if (StringUtils.hasText(packages))
                basePackages.add(packages);
        }
        return basePackages;
    }

    private Set<Class<?>> getClasses(List<String> basePackages) {
        Set<Class<?>> res = new HashSet<>();
        for (String str : basePackages) {
            String basePackage = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(str) + "/**/*.class";
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
            try {
                Resource[] resources = resolver.getResources(basePackage);
                for (Resource resource : resources) {
                    if (!resource.isReadable()) continue;
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    Class<?> forName = Class.forName(metadataReader.getClassMetadata().getClassName());
                    res.add(forName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return res;
    }

}
