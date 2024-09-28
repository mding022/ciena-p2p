package com.millerding.p2p;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class P2PFileSharingApplication {

	public static void main(String[] args) {
		SpringApplication.run(P2PFileSharingApplication.class, args);
	}

}
