package com.urlshortener.UrlShortener.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "user")
public class User implements Serializable {
 
    private static final long serialVersionUID = -3009157732242241606L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
 
    @Column(name = "firstname")
    private String firstName;
    
    @Column(name = "password")
    private String password;
 
    public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
 
    protected User() {
    }
 
    public User(String firstName, String password) {
        this.firstName = firstName;
        this.password = password;
    }
 
    @Override
    public String toString() {
        return String.format("User[id=%d, firstName='%s', password='%s']", id, firstName, password);
    }
}
