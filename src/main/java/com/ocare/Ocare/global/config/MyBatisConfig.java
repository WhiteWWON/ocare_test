package com.ocare.Ocare.global.config;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(
        basePackages = "com.ocare.Ocare.adapter.out.persistence.repository",
        annotationClass = Mapper.class  // 오직 @Mapper 어노테이션이 붙은 인터페이스만 스캔
)
public class MyBatisConfig {
    @Bean(name = "sqlSessionFactory")
    @Primary // 여러 개의 빈이 있을 경우 이 설정을 우선순위로
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // 1. XML 매퍼 위치 명시적 지정 (src/main/resources/mapper/ 하위 모든 xml)
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));

        // 2. SnakeCase -> CamelCase 설정
        org.apache.ibatis.session.Configuration mybatisConfig = new org.apache.ibatis.session.Configuration();
        mybatisConfig.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(mybatisConfig);

        return sessionFactory.getObject();
    }
}
