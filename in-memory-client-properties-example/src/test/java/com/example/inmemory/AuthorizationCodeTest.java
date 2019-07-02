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
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.deser.DataFormatReaders.Match;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class AuthorizationCodeTest {

	@Autowired
	MockMvc mvc;
	
	@Value("${security.oauth2.client.client-id}")
	private String CLIENT_ID;	//application.properties에 설정된 클라이언트 ID
	
	@Value("${security.oauth2.client.client-secret}")
	private String CLIENT_SECRET;	//application.properties에 설정된 클라이언트 secret
	
	/**
	 * 테스트 모듈 junit 테스트 가능하도록 @Test 어노테이션 작성
	 * @WithMockUser 어노테이션은 스프링 시큐리티의 유저를 생성하고 인증처리 까지 해주는 어노테이션
	 * 해당 어노테이션 사용 이유는 하기 /oauth/authorize 호출 시 spring security authenticate(인증)이 되어있지 않으면 로그인 페이지로 redirect 시키기 때문에
	 * 해당 URL 호출 전에 인증 처리 하는 용도로 사용.
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username="test1", password="test1", roles= {"ADMIN"})
	public void authorizationCodeTest() throws Exception {
		
		//Oauth 인가 처리 및 authorization_code 의 code 발급
		MultiValueMap<String, String> authorizeMap = authorize();
		
		log.debug(authorizeMap.toString());
		
		//Oauth Access token 발급 처리
		getAccessToken(authorizeMap);
	}
	
	/**
	 * 'grant_type'을 'authorization_code'로 사용 시 /oauth/authorize URL을 호출 하여 client 인가 후 발급된 코드로 Access Token을 요청해야함.
	 * @return
	 * @throws Exception
	 */
	private MultiValueMap<String, String> authorize() throws Exception {
		
		/**
		 * /oauth/authorize 요청 시 parameter
		 * grant_type - 권한 부여 유형 (authorization_code), 필수
		 * response_type - 응답 타입 (authorization_code는 무조건 code 만 입력 가능), 필수
		 * client_id - 클라이언트 ID(application.properties에 등록한 Client Id), 필수
		 * state - 인가 이후 호출된 요청에서 기존 state 요청의 state 인지 검증을 하기 위해 필요, 권장
		 * redirect_uri - 인가 이후 호출될 redirect_uri(application.properties에 등록된 redirect_uri가 다수 일 경우는 필수. 
		 * 				단, application.properties에 등록되어 있는 Uri 중 하나를 입력해야함.), 선택
		 * scope - 인가 범위, 선택
		 */
		
		MvcResult result = 
				mvc.perform(
						post("/oauth/authorize")
						.accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
						.param("grant_type", "authorization_code")
						.param("client_id", CLIENT_ID+"1")
						.param("response_type", "code")
						.param("state", "1234")
						)
					//.andExpect(ResultMatcher.matchAll(status().is3xxRedirection()))
					.andExpect(ResultMatcher.matchAll(status().isUnauthorized()))
					.andDo(print())
					.andReturn()
				;
		
		//
		return getResultMap(result.getResponse().getRedirectedUrl());
	}
	
	/**
	 * /oauth/authorize 인가 이후 redirect_uri 호출 시 query param으로 code 및 state 전송.
	 * 해당 code 및 state를 사용하기 위한 전 처리
	 * code는 1회성 이므로 사용 이후 혹은 파기 된 경우는 다시 인가 요청 필요.
	 * 인가 요청 시 state가 111 이었으면 인가 이후 response의 state에도 111이 넘어와야 함.
	 * ------------------------------------------------------------
	 * 인가 이후 success response
	 * code - 1회성 토큰 발급용 code
	 * state - 인가 요청 시 전달 된 state
	 * 
	 * @see AuthorizationEndpoint
	 * ------------------------------------------------------------
	 * 인가 이후 error response - /oauth/error 페이지로 forward 시킴
	 * error - 에러 코드
	 * error_description - 에러 설명
	 * 
	 * @see OAuth2Exception
	 * 
	 * @param redirectUrl
	 * @return
	 * @throws Exception
	 */
	private MultiValueMap<String, String> getResultMap(String redirectUrl) throws Exception {
		
		MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString(redirectUrl).build().getQueryParams();
		
		return queryParams;
	}
	
	/**
	 * Access Token 발급 로직
	 * /oauth/token 요청 시 필요 parameter
	 * grant_type - 권한 부여 유형 (authorization_code), 필수
	 * code - 인가 이후 발급 된 코드, 필수
	 * redirect_uri - Access Token 발급 이 후  돌아갈 URL(인가 요청 시 등록되었던 URL과 동일 해야한다.), 조건적 필수
	 * client_id - application.properties에 등록된 client id, 필수
	 * ---------------------------------------------------------------------------------
	 * access token 발급 시에는 client id와 secret으로 basic 인증이 필요. 때문에 헤더에 basic 인증 요청 추가 됨. 
	 * 
	 * @see TokenEndpoint
	 * 
	 * @param authorizeMap
	 * @throws Exception
	 */
	private void getAccessToken(MultiValueMap<String, String> authorizeMap) throws Exception {
		MvcResult result = 
				mvc.perform(
						post("/oauth/token")
						.with(httpBasic(CLIENT_ID, CLIENT_SECRET))
						.accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
						.param("grant_type", "authorization_code")
						.param("code", authorizeMap.getFirst("code"))
						.param("client_id", CLIENT_ID)
						).andExpect(status().isOk())
				.andDo(print())
				.andReturn()
				;
		
		/**
		 * response에 바디로 넘어오기 때문에 objectMapper 사용 추천 사용하기 나름.
		 */
		log.debug("\n get Access Token grant type by authorization_code : " + result.getResponse().getContentAsString());
	}
}
