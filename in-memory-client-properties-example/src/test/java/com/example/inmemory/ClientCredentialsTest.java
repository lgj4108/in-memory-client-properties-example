package com.example.inmemory;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Slf4j
public class ClientCredentialsTest {

	@Autowired
	MockMvc mvc;
	
	@Value("${security.oauth2.client.client-id}")
	private String CLIENT_ID;
	
	@Value("${security.oauth2.client.client-secret}")
	private String CLIENT_SECRET;
	
	/**
	 * grant_type이 client_credentials 로 Access_token 받기
	 * client Id 와 Secret으로 basic 인증 후 Access Token 발급 처리
	 * authorization_code, implict 와 달리 인가 없이 바로 token 발급 URL 호출
	 * ------------------------------------------------------------
	 * token 발급 시 header
	 * Authorization basic client_id:client_secret
	 * 'client_id:client_secret' 은 base64 로 인코딩 되어야 함.  
	 * 
	 * token 발급 시 Parameter
	 * grant_type - 토큰 발급 유형(client_credentials), 필수
	 * scope - 이용 가능 범위, 선택 
	 * 
	 * @throws Exception
	 */
	@Test
	public void getAccessTokenByClientCredentials() throws Exception {
		MvcResult result = 
				mvc.perform(
						post("/oauth/token")	//oauth token 발급 URL 호출
						.with(httpBasic(CLIENT_ID, CLIENT_SECRET))	//application.properties에 등록된 client_id, secret
						.accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
						.param("grant_type", "client_credentials")	//access token 발급 유형 
						.param("scope", "any")
						)
					.andExpect(status().isOk())
					.andDo(print())
					.andReturn()
					;
		
		log.debug("\n get Access Token grant type by client_credentials : " + result.getResponse().getContentAsString());
	}
}
