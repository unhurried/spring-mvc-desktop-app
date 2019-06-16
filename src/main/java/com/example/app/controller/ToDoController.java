package com.example.app.controller;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Conventions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.app.controller.form.ToDoForm;
import com.example.app.repository.ToDoRepository;
import com.example.app.repository.entity.ToDo;

/** A controller class that assigns HTTP requests to Java methods. */
@Controller
@RequestMapping("/todos")
public class ToDoController {

	@Autowired
	private ToDoRepository repository;

	/** Show a list of registered items. */
	@GetMapping()
	private String list(Model model) {
		Iterable<ToDo> toDoList = repository.findAll();
		model.addAttribute(toDoList);
		return "todo/list";
	}

	/** Show a form to update an item. */
	@GetMapping("/{id}")
	public String get(Model model, @PathVariable("id") String id) {
		Optional<ToDo> optional = repository.findById(Long.valueOf(id));

		optional.ifPresent(todo -> {
			ToDoForm form = new ToDoForm();
			form.setId(Long.valueOf(id));
			BeanUtils.copyProperties(todo, form);
			model.addAttribute(form);
		});

		return optional.isPresent()? "todo/form" : "redirect:/todos";
	}

	/** Show a form to register an item. */
	@GetMapping("/form") // equivalent to @RequestMapping("/form")
	// Defining a ToDoForm argument will clear the BindingResult set in "submit" method.
	// To avoid it, use a Model arguent to get the ToDoForm object as follows:
	// model.asMap().get(ClassUtils.getShortNameAsProperty(ToDoForm.class);
	public String form(Model model) {

		// Initialize the ToDoForm model as it is used in "form" template.
		// Otherwise, NullPointerException will be thrown on the first access.
		if (!model.containsAttribute(ClassUtils.getShortNameAsProperty(ToDoForm.class))) {
			model.addAttribute(new ToDoForm());
		}

		return "todo/form";
	}

	/** Receive the form data submitted with POST method. */
	@PostMapping("/submit") // equivalent to @RequestMapping(path="/submit", method=RequestMethod.POST)
	// Spring MVC sets validation reuslts in a BindingResult argument declared after the form argument to validate.
	public String submit(@Validated ToDoForm mf, BindingResult br, RedirectAttributes ra) {

		if (br.hasErrors()) {
			// Add the BindingResult object to Flash Attribute to take it over after redirect.
			// Use the following key name rule so that the BindingResult object can be used in Thymeleaf templates.
			// org.springframework.validation.BindingResult.{class name (camel case)}
			ra.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + Conventions.getVariableName(mf), br);
			ra.addFlashAttribute("error", "Please correct the errors indicated below.");

		} else {
			ToDo todo = new ToDo();
			BeanUtils.copyProperties(mf, todo);
			repository.save(todo);
			mf.setId(todo.getId());
			ra.addFlashAttribute("message", "The item has been created / updated successfully.");
		}

		// Add the ToDoForm object to Flash Attribute to take it over after redirect.
		ra.addFlashAttribute(mf);
		return mf.getId() == null? "redirect:/todos/form" : "redirect:/todos/" + mf.getId();
	}

	/** Delete an item. */
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable("id") String id) {
		repository.deleteById(Long.valueOf(id));
		return "redirect:/todos";
	}
}
