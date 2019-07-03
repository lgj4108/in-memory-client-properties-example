package com.example.inmemory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class ImplictTest {

	@Autowired
	MockMvc mvc;
	
	@Value("${security.oauth2.client.client-id}")
	private String CLIENT_ID;
	
	@Value("${security.oauth2.client.client-secret}")
	private String CLIENT_SECRET;
	
	/**
	 * grant_type implict으로 액세스 토큰 발급
	 * implict는 인가 처리 이후 즉시 토큰을 발급한다. (따로 token 발급 요청을 하지 않아도 됨)
	 * 
	 * /oauth/authorize 요청 시 parameter
	 * grant_type - 토튼 발급 유형, 필수
	 * response_type - 응답 타입(implict 일 경우 token 만 지원), 필수
	 * client_id - application.properties에 등록된 client Id, 필수
	 * state - 상태 값, 권장사항
	 * redirect_uri - 인가 이후 호출될 redirect_uri(application.properties에 등록된 redirect_uri가 다수 일 경우는 필수. 
	 * 				단, application.properties에 등록되어 있는 Uri 중 하나를 입력해야함.), 선택
	 * scope - 인가 범위, 선택
	 * 
	 * ---------------------------------------------------------------------
	 * 
	 * 인가 후 authorization_code처럼 redirect_uri를 호출하지만 토큰 및 기타 response data 들이 hash string으로 넘어옴
	 * 이유 - queryparam으로 redirect_uri 호출 시 파라메터로 사용 가능하기 때문에 그것을 방지 하기 위해 hash string으로 넘김.
	 * ex) http://localhost/auth/redirect#access_token=31d25972-1d31-4cb9-b5a3-0a2674ef22eb&token_type=bearer&expires_in=43199
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username="test1", password="test1", roles= {"ADMIN"})
	public void implictTest() throws Exception {
		MvcResult result = mvc.perform(
				get("/oauth/authorize")
					.accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
					.param("grant_type", "implict")
					.param("response_type", "token")
					.param("client_id", CLIENT_ID)
					.param("scope", "any"))
			.andExpect(status().is3xxRedirection())
			.andDo(print())
			.andReturn();
		
		String fragmentAccessToken 
			= UriComponentsBuilder.fromUriString(result.getResponse().getRedirectedUrl()).build().getFragment();
		
		log.debug("\n get Access Token grant type by implict : " + fragmentAccessToken);
		
	}
}
