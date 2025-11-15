package com.mani.payment_transfer_system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PaymentTransferSystemApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testMainMethod() {
		// Test that main method can be called without errors
		// This covers the main method lines for 100% coverage
		String[] args = {};
		PaymentTransferSystemApplication.main(args);
	}

}
