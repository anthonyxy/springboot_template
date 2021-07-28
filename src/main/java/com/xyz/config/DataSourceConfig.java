package com.xyz.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@MapperScan(value = DataSourceConfig.MAPPER_LOCATION)
public class DataSourceConfig {

	static final String MAPPER_LOCATION = "com.xyz.*.mapper";

	@Value("${mysql.datasource.url}")
	private String url;

	@Value("${mysql.datasource.username}")
	private String username;

	@Value("${mysql.datasource.password}")
	private String password;

	@Value("${mysql.datasource.driverClassName}")
	private String driverClassName;

	@Value("${druid.datasource.initialSize}")
	private int initialSize;

	@Value("${druid.datasource.minIdle}")
	private int minIdle;

	@Value("${druid.datasource.maxActive}")
	private int maxActive;

	@Value("${druid.datasource.maxWait}")
	private int maxWait;

	@Value("${druid.datasource.timeBetweenEvictionRunsMillis}")
	private int timeBetweenEvictionRunsMillis;

	@Value("${druid.datasource.minEvictableIdleTimeMillis}")
	private int minEvictableIdleTimeMillis;

	@Value("${druid.datasource.validationQuery}")
	private String validationQuery;

	@Value("${druid.datasource.testWhileIdle}")
	private boolean testWhileIdle;

	@Value("${druid.datasource.testOnBorrow}")
	private boolean testOnBorrow;

	@Value("${druid.datasource.testOnReturn}")
	private boolean testOnReturn;

	@Value("${druid.datasource.poolPreparedStatements}")
	private boolean poolPreparedStatements;

	@Value("${druid.datasource.maxPoolPreparedStatementPerConnectionSize}")
	private int maxPoolPreparedStatementPerConnectionSize;

	@Value("${druid.datasource.filters}")
	private String filters;

	@Value("{druid.datasource.connectionProperties}")
	private String connectionProperties;

	@Bean(name = "dataSource")
	public DruidDataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setDriverClassName(driverClassName);
		dataSource.setInitialSize(initialSize);
		dataSource.setMinIdle(minIdle);
		dataSource.setMaxActive(maxActive);
		dataSource.setMaxWait(maxWait);
		dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		dataSource.setValidationQuery(validationQuery);
		dataSource.setTestWhileIdle(testWhileIdle);
		dataSource.setTestOnBorrow(testOnBorrow);
		dataSource.setTestOnReturn(testOnReturn);
		dataSource.setPoolPreparedStatements(poolPreparedStatements);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
		dataSource.setConnectionProperties(connectionProperties);
		return dataSource;
	}

	@Bean(name = "transactionManager")
	public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DruidDataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	// MyBatis的sqlSessionFactory
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DruidDataSource dataSource) throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources(DataSourceConfig.MAPPER_LOCATION));
		return sessionFactory.getObject();
	}

}
