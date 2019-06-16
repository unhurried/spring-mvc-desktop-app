package com.example.app.repository.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/** A JPA entity corresponding to todo table */
// @Entity: Indicate that the class is a JPA entity.
@Entity
// @Table: Specifies the table name if it is not same as the class name.
@Table(name="todo")
@Getter
@Setter
public class ToDo {
	// @Id: Indicates that the field uniquely identifies the entity.
    @Id
    // @GeneratedValue(strategy=GenerationType.IDENTITY):
    //   Generates the primary key by using the identity columns of RDBMS.
    //   (ex. MySQL: auto_increment)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
	private String title;
	private String category;
	private String content;
}