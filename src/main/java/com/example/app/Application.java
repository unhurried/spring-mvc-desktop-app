package com.example.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.example.app.gui.ConsoleFrame;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws Exception {
		// Show a console window when application is started by clicking a jar file.
	    if (args.length == 0) {
	        ConsoleFrame.show();
	    }

	    // Prevent Spring application from running in headless mode.
        new SpringApplicationBuilder(Application.class).headless(false).run(args);
	}
}
