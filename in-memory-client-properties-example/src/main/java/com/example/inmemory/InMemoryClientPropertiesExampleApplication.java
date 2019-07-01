package com.example.inmemory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;

/**
 * Spring Boot properties의 클라이언트 ID 와 Secret으로 토큰 발급하기
 * @param args
 */
@EnableAuthorizationServer
@SpringBootApplication
public class InMemoryClientPropertiesExampleApplication {

	/**
	 * 별다른 java configuration 없이 토큰 발급 URL을 호출하여 토큰을 발급 함.
	 * Spring boot 에서는 @EnableAuthorizationServer 어노테이션을 제공하여 기본 Oauth인증 Configuration을 사용할 수 있음.
	 * 기본 인증 configuration은 하기 java 라이브러리 파일 확인
	 * @see OAuth2AuthorizationServerConfiguration 
	 * 상기 configuration에 BaseClientDetails oauth2ClientDetails() 메소드가 @Bean 
	 * 및 @ConfigurationProperties(prefix = "security.oauth2.client")로 등록되어있어
	 * application.properties에 등록되어 있는 클라이언트 ID 와 Secret을 참조함.
	 * 
	 * token URL 호출 시 tokenEndpoint에서 grant type 별 access_token 발급 프로바이더로 전달 하여 access_token 발급
	 * @see TokenEndpoint - getTokenGranter().grant(tokenRequest.getGrantType(), tokenRequest)
	 * 상기 grant 호출 시 CompositeTokenGranter 의 grant에서 for문을 돌며 grant_type 비교 후 해당 provider를 호출 후 access_token return
	 */
	public static void main(String[] args) {
		SpringApplication.run(InMemoryClientPropertiesExampleApplication.class, args);
	}

	
	
}
