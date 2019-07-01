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
	
	@Test
	public void passwordTest() throws Exception {
		MvcResult result = 
				mvc.perform(
						post("/oauth/token")
						.with(httpBasic("foo", "bar"))
						.accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
						.param("grant_type", "password")
						.param("username", "test")
						.param("password", "test")
						.param("scope", "any")
						).andExpect(status().isOk()).andReturn();
		
		log.debug("\n get Access Token grant type by password : " + result.getResponse().getContentAsString());
	}
}
