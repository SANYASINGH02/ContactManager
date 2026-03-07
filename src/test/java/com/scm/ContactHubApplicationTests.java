package com.scm;

import com.scm.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ContactHubApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private EmailService emailService;

//	@Test
//	public void emailSendTest(){
//		emailService.sendEmail("ssanyatwoz@gmail.com","test email: SCM","hello this is testing email to verify account");
//	}
}
