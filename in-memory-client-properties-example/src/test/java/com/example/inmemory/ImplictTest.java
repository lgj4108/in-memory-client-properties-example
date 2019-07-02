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
