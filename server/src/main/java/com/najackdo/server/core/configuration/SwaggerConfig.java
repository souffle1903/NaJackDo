package com.najackdo.server.core.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		String jwt = "JWT";
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
		Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
			.name(jwt)
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
		);

		Server server = new Server();
		server.setUrl("https://www.najackdo.kro.kr"); // https://에 접근 가능하게 설정

		return new OpenAPI()
			.components(new Components())
			.info(apiInfo())
			.addSecurityItem(securityRequirement)
			.components(components)
			.servers(List.of(server));
	}
	private Info apiInfo() {
		return new Info()
			.title("나작도 API 문서") // API의 제목
			.version("1.0.1"); // API의 버전
	}




}
