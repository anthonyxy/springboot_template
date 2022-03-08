package com.xyz.util.dto;

public class Account {
	
	 private Long id;
	 
	 private String username;
	 
	 private Integer role;
	 
	 private Long fcompanyId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public Long getFcompanyId() {
		return fcompanyId;
	}

	public void setFcompanyId(Long fcompanyId) {
		this.fcompanyId = fcompanyId;
	}
	 
	 
	 

}
