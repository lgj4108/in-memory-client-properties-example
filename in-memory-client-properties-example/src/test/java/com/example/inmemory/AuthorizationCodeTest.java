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
public class AuthorizationCodeTest {

	@Autowired
	MockMvc mvc;
	
	@Value("${security.oauth2.client.client-id}")
	private String CLIENT_ID;
	
	@Value("${security.oauth2.client.client-secret}")
	private String CLIENT_SECRET;
	
	@Test
	@WithMockUser(username="test1", password="test1", roles= {"ADMIN"})
	public void authorizationCodeTest() throws Exception {
		
		MultiValueMap<String, String> authorizeMap = authorize();
		
		log.debug(authorizeMap.toString());
		
		getAccessToken(authorizeMap);
	}
	
	private MultiValueMap<String, String> authorize() throws Exception {
		MvcResult result = 
				mvc.perform(
						post("/oauth/authorize")
						.accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
						.param("grant_type", "authorization_code")
						.param("client_id", CLIENT_ID)
						.param("response_type", "code")
						.param("state", "1234")
						).andExpect(status().is3xxRedirection())
				.andDo(print())
				.andReturn()
				;
		
		return getResultMap(result.getResponse().getRedirectedUrl());
	}
	
	private MultiValueMap<String, String> getResultMap(String redirectUrl) throws Exception {
		
		MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString(redirectUrl).build().getQueryParams();
		
		return queryParams;
	}
	
	private void getAccessToken(MultiValueMap<String, String> authorizeMap) throws Exception {
		MvcResult result = 
				mvc.perform(
						post("/oauth/token")
						.with(httpBasic(CLIENT_ID, CLIENT_SECRET))
						.accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
						.param("grant_type", "authorization_code")
						.param("code", authorizeMap.getFirst("code"))
						.param("client_id", CLIENT_ID)
						.param("state", "1234")
						).andExpect(status().isOk())
				.andDo(print())
				.andReturn()
				;
		
		log.debug("\n get Access Token grant type by authorization_code : " + result.getResponse().getContentAsString());
	}
}
