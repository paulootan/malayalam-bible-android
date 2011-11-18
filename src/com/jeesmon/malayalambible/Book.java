package com.jeesmon.malayalambible;

import java.io.Serializable;

public class Book implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;	
	private int chapters;
	
	public Book(int id, String name, int chapters) {
		this.id = id;
		this.name = name;
		this.chapters = chapters;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getChapters() {
		return chapters;
	}

	public void setChapters(int chapters) {
		this.chapters = chapters;
	}
}
