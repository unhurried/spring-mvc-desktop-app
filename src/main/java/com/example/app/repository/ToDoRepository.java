package com.example.app.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.app.repository.entity.ToDo;

/** Spring Data JPA repository for ToDoEntity */
public interface ToDoRepository extends PagingAndSortingRepository<ToDo, Long> {}