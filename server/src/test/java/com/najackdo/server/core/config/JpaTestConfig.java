// package com.najackdo.server.core.config;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.TestConfiguration;
// import org.springframework.context.annotation.Bean;
// import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//
// import com.querydsl.jpa.impl.JPAQueryFactory;
//
// import jakarta.persistence.EntityManager;
//
// @TestConfiguration
// @EnableJpaAuditing
// public class JpaTestConfig {
// 	@Autowired
// 	private EntityManager em;
//
// 	@Bean
// 	public JPAQueryFactory queryFactory() {
// 		return new JPAQueryFactory(em);
// 	}
// }
