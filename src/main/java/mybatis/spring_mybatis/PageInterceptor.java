package mybatis.spring_mybatis;

import mybatis.bean.PageList;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZYB
 * @since 2016-12-28 上午9:06
 */
@Intercepts({
    @Signature(method = "query", type = Executor.class, args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class PageInterceptor implements Interceptor {

    private static Map<String, MappedStatement> mappedStatementMap = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public Object intercept(Invocation invocation) throws Throwable {
        Executor executor = (Executor) invocation.getTarget();
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        PageList pageList = null;
        if (parameter instanceof PageList)
            pageList = (PageList) parameter;

        if (parameter instanceof HashMap) {
            HashMap hashMap = (HashMap) parameter;
            Optional first = hashMap.values().stream().filter(val -> val instanceof PageList).findFirst();
            pageList = first.isPresent() ? (PageList) first.get() : null;
        }
        if (pageList == null)
            return invocation.proceed();


        Configuration configuration = mappedStatement.getConfiguration();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        MappedStatement ms = mappedStatementMap.get(mappedStatement.getId());
        if (ms == null) {
            String countMsId = mappedStatement.getId() + "_COUNT";
            String resultMapId = countMsId + "_RESULT";
            ResultMap resultMap = new ResultMap.Builder(configuration, resultMapId, Integer.class, Collections.emptyList()).build();
            ms = new MappedStatement.Builder(configuration, countMsId, mappedStatement.getSqlSource(), SqlCommandType.SELECT)
                .useCache(false).statementType(mappedStatement.getStatementType()).resultMaps(Collections.singletonList(resultMap)).build();
            mappedStatementMap.put(mappedStatement.getId(), ms);
        }
        String sql = "select count(*) as count from (" + boundSql.getSql() + ") as temp";
        BoundSql countBoundSql = new BoundSql(configuration, sql, boundSql.getParameterMappings(), parameter);
        CacheKey cacheKey = executor.createCacheKey(ms, parameter, RowBounds.DEFAULT, countBoundSql);
        List<Object> countList = executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, countBoundSql);
        if (countList == null || countList.size() != 1 || (int) countList.get(0) == 0) {
            pageList.setList(Collections.EMPTY_LIST);
            return Collections.EMPTY_LIST;
        }
        int page = pageList.getPage();
        int perPage = pageList.getPerPage();

        int row = (int) countList.get(0);
        pageList.setRowCount(row);
        pageList.setPageCount(row % perPage == 0 ? row / perPage : row / perPage + 1);
        if (page > pageList.getPageCount()) {
            pageList.setList(Collections.EMPTY_LIST);
            return Collections.EMPTY_LIST;
        }

        StringBuffer sb = new StringBuffer(boundSql.getSql());
        if (sb.charAt(sb.length() - 1) == ';')
            sb.deleteCharAt(sb.length() - 1);
        if (sb.charAt(sb.length() - 1) != ' ')
            sb.append(' ');
        sb.append("limit ").append((page - 1) * perPage).append(", ").append(perPage).append(";");
        BoundSql limitBoundSql = new BoundSql(configuration, sb.toString(), boundSql.getParameterMappings(), parameter);
        CacheKey limitCacheKey = executor.createCacheKey(ms, parameter, RowBounds.DEFAULT, limitBoundSql);
        List<Object> query = executor.query(mappedStatement, parameter, RowBounds.DEFAULT, resultHandler, limitCacheKey, limitBoundSql);
        pageList.setList(query);
        return query;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
