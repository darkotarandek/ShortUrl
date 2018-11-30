package com.urlshortener.UrlShortener.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "url")
public class URL implements Serializable{

	private static final long serialVersionUID = -3009157732242331606L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
 
    @Column(name = "firstname")
    private String firstName;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "longurl")
    private String longUrl;
    
    @Column(name = "shorturl")
    private String shortUrl;
    
    @Column(name = "redirectType")
    private int redirectType;

	@Column(name = "count")
    private int count;
 
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

	public String getLongUrl() {
		return longUrl;
	}

	public void setLongUrl(String longUrl) {
		this.longUrl = longUrl;
	}
	
	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public int getRedirectType() {
		return redirectType;
	}

	public void setRedirectType(int redirectType) {
		this.redirectType = redirectType;
	}
 
    protected URL() {
    }
 
    public URL(String firstName, String password, String longUrl, String shortUrl, int count, int redirectType) {
        this.firstName = firstName;
        this.password = password;
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        this.count = count;
        this.redirectType = redirectType;
    }
 
    @Override
    public String toString() {
        return String.format("URL[id=%d, password='%s', firstName='%s', longUrl='%s', shortUrl='%s', count='%d', redirectType='%d']", id, password, firstName, longUrl, shortUrl, count, redirectType);
    }
	
}
