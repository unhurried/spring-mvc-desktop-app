package com.example.app.controller;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Conventions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

	/** Show a form to register an item. */
	// This controller shows a blank form when a user visits for the first time, but when he already sent input to
	// "submit" method and it has some erros, the controller needs to take over the previous input and show them with
	// error messages.
	// To handle both cases in the same Thymeleaf template (form.html), the controller needs to set up a ToDoForm model
	// even for the first visit. (Otherwise, rendering the template will throw NullPointerException.)
	// However, defining a ToDoForm argument to initilize a ToDoForm model for the first visit will set a blank
	// BindingResult instance to Model (even if the ToDoForm argument is not annotated with @Validated or BindingResult
	// is not declared as an argument). That results in error functions in Thymeleaf (such as th:errors or
	// #fields.hasErrors) to refer to the blank BindingResult instance in Model over one in FlashAttributes taken over
	// from "submit" method.
	// To avoid this, Declare only a Model argument and manually initialize ToDoForm model for the first visit.
	@GetMapping("/create") // or @RequestMapping("/create")
	public String create(Model model) {
		// Initialize a ToDoForm model for the first visit.
		if (!model.containsAttribute("error")) {
			model.addAttribute(new ToDoForm());
		}
		// It is also possible to check whether ToDoForm model already exists using Model interface.
		//if (!model.containsAttribute(ClassUtils.getShortNameAsProperty(ToDoForm.class))) {
		//	model.addAttribute(new ToDoForm());
		//}

		return "todo/form";
	}
	// Alternatively, it is possible to declare a ToDoForm argument (to omit the manual initialization) and manually
	// override the BindingResult model with an instance in FlashAttributes as follows:
	/*
	public String create(ToDoForm form, Model model, HttpServletRequest hsr) {
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(hsr);
		if (inputFlashMap != null) {
			BindingResult br = (BindingResult) inputFlashMap.get(BindingResult.MODEL_KEY_PREFIX + Conventions.getVariableName(form));
			model.addAttribute(BindingResult.MODEL_KEY_PREFIX + Conventions.getVariableName(form), br);

		}
		return "todo/form";
	}
	*/

	/** Show a form to update an item. */
	@GetMapping("/{id}")
	public String get(@PathVariable("id") String id, Model model) {
		// Omit repository access when redirected from "submit" method with error messages.
		if (model.containsAttribute("error")) {
			return "todo/form";
		}

		Optional<ToDo> optional = repository.findById(Long.valueOf(id));

		optional.ifPresent(todo -> {
			ToDoForm form = new ToDoForm();
			form.setId(Long.valueOf(id));
			BeanUtils.copyProperties(todo, form);
			model.addAttribute(form);
		});

		return optional.isPresent()? "todo/form" : "redirect:/todos";
	}

	/** Receive form data submitted with POST method and create or update a model. */
	@PostMapping("/submit") // or @RequestMapping(path="/submit", method=RequestMethod.POST)
	// Spring MVC sets validation reuslts in a BindingResult argument declared after the form argument to validate.
	public String submit(@Validated ToDoForm form, BindingResult br, RedirectAttributes ra) {
		boolean isCreate = form.getId() == null;

		if (br.hasErrors()) {
			// Add the BindingResult object to FlashAttribute to take it over after the redirection.
			// Use the following key name rule so that the BindingResult object can be used in Thymeleaf templates:
			//   org.springframework.validation.BindingResult.{class name (camel case)}
			ra.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + Conventions.getVariableName(form), br);
			ra.addFlashAttribute("error", "Please correct the errors indicated below.");
			// Add the ToDoForm object to Flash Attribute to take it over after redirect.
			ra.addFlashAttribute(form);
			return isCreate? "redirect:/todos/create" : "redirect:/todos/" + form.getId();

		} else {
			ToDo todo = new ToDo();
			BeanUtils.copyProperties(form, todo);
			repository.save(todo);
			form.setId(todo.getId());
			String message = isCreate?
					"The item has been created successfully." : "The item has been updated successfully.";
			ra.addFlashAttribute("message", message);
			return "redirect:/todos/" + form.getId();
		}
	}

	/** Delete an item. */
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable("id") String id) {
		repository.deleteById(Long.valueOf(id));
		return "redirect:/todos";
	}
}
