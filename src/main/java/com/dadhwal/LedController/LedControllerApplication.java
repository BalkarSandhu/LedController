package com.dadhwal.LedController;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class LedControllerApplication {

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(LedControllerApplication.class, args);
	}

	public static void restart() {
		Thread restartThread = new Thread(LedControllerApplication::run);

		restartThread.setDaemon(false);
		restartThread.start();
	}

	private static void run() {
		try {
			System.out.println("Stopping application...");
			context.close();
			System.out.println("Application stopped.");

			// Delay to ensure the context is fully closed before restarting
			Thread.sleep(1000);  // Optional delay for cleanup

			System.out.println("Starting application...");
			context = SpringApplication.run(LedControllerApplication.class);
		} catch (InterruptedException e) {

			Thread.currentThread().interrupt();
		}
	}

}
