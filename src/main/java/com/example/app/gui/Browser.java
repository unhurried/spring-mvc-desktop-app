package com.example.app.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/** A command line component that opens the application URL in default browser. */
@Component
public class Browser implements CommandLineRunner {

	@Autowired
	private ServletWebServerApplicationContext context;

	@Override
	public void run(String... args) throws IOException {
		Desktop desktop = Desktop.getDesktop();
		URI uri = UriComponentsBuilder
				.fromUriString("http://localhost/todos")
				.port(context.getWebServer().getPort())
				.build().toUri();
		desktop.browse(uri);
	}
}
