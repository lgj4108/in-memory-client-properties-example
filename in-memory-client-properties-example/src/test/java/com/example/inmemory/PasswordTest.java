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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class PasswordTest {

	@Autowired
	MockMvc mvc;
	
	@Value("${security.oauth2.client.client-id}")
	private String CLIENT_ID;
	
	@Value("${security.oauth2.client.client-secret}")
	private String CLIENT_SECRET;
	
	/**
	 * grant_type이 password 로 Access_token 발급
	 * client_credentials 와 마찬 가지로 client id, secret으로 basic 인증 처리
	 * 다른 점은 사용자의 아이디와 패스워드를 파라메터로 받고 있다는게 다름. 
	 * 해당 아이디 패스워드로 실제 사용자인지 조회 후 토큰 발급
	 * 그 외엔 client_credentials 과 동일  
	 * ----------------------------------------------------------------
	 * token 발급 시 parameter
	 * grant_type - 토큰 발급 유형(client_credentials), 필수
	 * username - 스프링 시큐리티에서 사용되어지는 사용자 ID, 필수
	 * password - 스프링 시큐리티에서 사용되는 사용자 ID에 해당되는 password, 필수
	 * scope - 이용 가능 범위, 선택 
	 * 
	 * @throws Exception
	 */
	@Test
	public void passwordTest() throws Exception {
		MvcResult result = 
				mvc.perform(
						post("/oauth/token")
						.with(httpBasic(CLIENT_ID, CLIENT_SECRET))
						.accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
						.param("grant_type", "password")
						.param("username", "test")
						.param("password", "test")
						.param("scope", "any")
						)
					.andExpect(status().isOk())
					.andDo(print())
					.andReturn()
					;
		
		log.debug("\n get Access Token grant type by password : " + result.getResponse().getContentAsString());
	}
}
