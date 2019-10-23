package com.example.app.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

/** A swing frame that shows console output. */
public class ConsoleFrame {

	public static void show() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				_show();
			}
		});
	}

	private static void _show() {
		JTextArea textArea = new JTextArea(20, 100);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		textArea.setEditable(false);

		// Enable auto scrolling.
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		TextAreaOutputStream taOutputStream = new TextAreaOutputStream(textArea);
		System.setOut(new PrintStream(taOutputStream, true));

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JScrollPane(textArea));

		JFrame frame = new JFrame("Console");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.pack();
		// Locate the frame in the center of the screen.
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	static class TextAreaOutputStream extends OutputStream {
		private final JTextArea textArea;

		public TextAreaOutputStream(final JTextArea textArea) {
			this.textArea = textArea;
		}

		@Override
		public void write(int b) throws IOException {
			textArea.append(String.valueOf((char) b));
		}
	}
}
