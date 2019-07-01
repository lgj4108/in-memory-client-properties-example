package com.example.inmemory;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
	
	@Test
	public void getAccessTokenByClientCredentials() throws Exception {
		MvcResult result = mvc.perform(
				post("/oauth/token")	//oauth token 발급 URL 호출
					//.with(httpBasic("foo", "bar"))	//application.properties에 등록된 client_id, secret
					.with(httpBasic(CLIENT_ID, CLIENT_SECRET))	//application.properties에 등록된 client_id, secret
					//.headers(headers)
					.accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
					.param("grant_type", "client_credentials")	//access token 발급 유형 
					.param("scope", "any")
					).andExpect(status().isOk())
					.andReturn()
					;
		
		log.debug("\n get Access Token grant type by client_credentials : " + result.getResponse().getContentAsString());
	}
}
