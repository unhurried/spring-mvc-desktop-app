package com.example.app.controller.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

/** A bean that handles form data. */
@Data
public class ToDoForm {
	private Long id;

	// Validation can be configured with JSR303 Bean Validation annotations.
	// cf. https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/Bean_Validation_Cheat_Sheet.md
	@NotNull
	@Size(min = 1, max = 31)
	private String title;

	@NotNull
	@Pattern(regexp = "(one|two|three)")
	private String category;

	@Size(max = 127)
	private String content;
}
