package com.example.qianziapp.bean;

public class Bean {
	String url;
	String content;
	int id;
	
	public Bean() {
	}

	public Bean(String url, String content,int id) {
		super();
		this.url = url;
		this.content = content;
		this.id=id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
