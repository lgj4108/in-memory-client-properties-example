package com.example.inmemory.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@RequestMapping("/redirect")
	public void redirectOauth(@RequestParam("code") String code) {
		
	}
}
