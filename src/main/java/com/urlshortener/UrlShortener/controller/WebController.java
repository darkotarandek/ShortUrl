package com.urlshortener.UrlShortener.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.json.*;

import com.urlshortener.UrlShortener.model.User;
import com.urlshortener.UrlShortener.repository.UserRepository;
import com.urlshortener.UrlShortener.repository.URLRepository;
import com.urlshortener.UrlShortener.service.RandomGenerator;
import com.urlshortener.UrlShortener.model.URL;

@Controller
public class WebController {
    
    private final UserRepository userRepository;
	private final URLRepository urlRepository;

	@Autowired
	WebController(UserRepository userRepository, URLRepository urlRepository) {
		this.userRepository = userRepository;
		this.urlRepository = urlRepository;
	}
    
    @PostMapping("/account")
    public ResponseEntity<?> newUser(@RequestBody String name) {
    	    	
    	JSONObject jsonName = new JSONObject(name);
    	boolean hasAccountId = jsonName.has("AccountId"); // checking if AccountId property exists in the JSON Request Body
    	String userName = "";
		String registeredUser = "";
		RandomGenerator randomAlphaNum = new RandomGenerator();
		HashMap<String, String> collection = new HashMap<>();
    	
    	if(hasAccountId == true) {
    		try {
    			userName = jsonName.getString("AccountId");
    		} catch (JSONException e) {
    			e.printStackTrace();
    			collection.put("description", "AccountId must be a string.");
            	return new ResponseEntity<>(collection, HttpStatus.BAD_REQUEST);
    		}
    	} else {
    		userName = "";
    	}

        String password = randomAlphaNum.randomString(8).toString();
        
        if(userName != "") {
        	
        	// looping through the users to find the registeredUser
        	for(User user: userRepository.findByFirstName(userName)){
        		registeredUser = user.getFirstName();
            }
        	
        	// checking to see if the user is registered or not
        	if(registeredUser == "") {
            	try {
            		collection.put("Success", "true");
                	collection.put("Description", "Your account is opened.");
                	collection.put("Password", password);
                	userRepository.save(new User(userName, password));
                	return new ResponseEntity<>(collection, HttpStatus.CREATED);
            	} catch(Exception e) {
            		e.printStackTrace();
                	return new ResponseEntity<>(collection, HttpStatus.INTERNAL_SERVER_ERROR);
            	}
            } else {
            	collection.put("Success", "false");
            	collection.put("Description", "Account already exists with that ID.");
            	return new ResponseEntity<>(collection, HttpStatus.CONFLICT);
            }
        } else {
        	collection.put("Description", "Please define AccountId correctly.");
        	return new ResponseEntity<>(collection, HttpStatus.BAD_REQUEST);
        }        
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUrl(@RequestBody String url, HttpServletRequest request, HttpServletResponse response){
    	JSONObject jsonUrl = new JSONObject(url);
    	String longUrl = "";
    	int redirectType = 302;
    	String registeredUser = "";
    	String registeredUserPassword = "";
    	String userPart = "";
		String passwordPart = "";
		String[] headerParts;
    	HashMap<String, String> collection = new HashMap<>();
    	RandomGenerator randomAlphaNum = new RandomGenerator();
    	
    	// check to see if the Authorization header is set
    	if(request.getHeader("Authorization") != null) {
    		String requestHeaderValue = request.getHeader("Authorization").toString();
    		
    		// splitting the header to extract the key:value part
    		headerParts = requestHeaderValue.split(":");
    		
    		// check to see if the header is set correctly
			if(headerParts.length != 1) {
    			userPart = headerParts[0];
        		passwordPart = headerParts[1];
			} else {
				collection.put("Description", "Authorization header is not set correctly.");
	    		return new ResponseEntity<>(collection, HttpStatus.BAD_REQUEST);
			}
    		
			// check to see if the user from the header exists in our database
    		for(User user: userRepository.findByFirstName(userPart)){
    			registeredUser = user.getFirstName();
            	if(user.getPassword().equals(passwordPart)) {
            		registeredUserPassword = passwordPart;
            		boolean hasLongUrl = jsonUrl.has("url");
                	boolean hasRedirectType = jsonUrl.has("redirectType");
                	
                	// check to see if url property exists in the JSON Request Body
                	if(hasLongUrl) {
                		try {
                    		longUrl = jsonUrl.getString("url");
                		} catch(JSONException e) {
                			e.printStackTrace();
                			collection.put("Description", "Url must be a string.");
                        	return new ResponseEntity<>(collection, HttpStatus.BAD_REQUEST);
                		}
                	}
                	
                	// check to see if redirectType property exists in the JSON Request Body
                	if(hasRedirectType) {
                		try {
                        	redirectType = jsonUrl.getInt("redirectType");
                		} catch(JSONException e) {
                			e.printStackTrace();
                			collection.put("Description", "redirectType must be an integer.");
                        	return new ResponseEntity<>(collection, HttpStatus.BAD_REQUEST);
                		}
                	} else {
                		redirectType = 302;
                	}
                	
                	if(longUrl != "") {
	                	String currentUrl = request.getRequestURL().toString(); // extracting the current url in the browser    
	                    String shortUrl = currentUrl.replace("register", randomAlphaNum.randomString(8).toString()); // replacing the /register part with random alphanumeric signs. For example: 'https://localhost:8080/register' will be replaced by 'https://localhost:xr4357A1'

	                    try {
	                    	collection.put("shortUrl", shortUrl);
	                    	urlRepository.save(new URL(registeredUser, registeredUserPassword, longUrl, shortUrl, 1, redirectType)); // saving our link in the database
	                    	return new ResponseEntity<>(collection, HttpStatus.CREATED);
	                    } catch(Exception e) {
	                    	e.printStackTrace();
	                    	return new ResponseEntity<>(collection, HttpStatus.INTERNAL_SERVER_ERROR);
	                    }
	                    
                	} else {
                    	collection.put("Description", "Please define Url correctly.");
                    	return new ResponseEntity<>(collection, HttpStatus.BAD_REQUEST);
                    }
            	} else {
            		collection.put("Description", "Not Authenticated.");
            		return new ResponseEntity<>(collection, HttpStatus.UNAUTHORIZED);
            	}
            }
    	} else {
    		collection.put("Description", "Authorization header is not set.");
    		return new ResponseEntity<>(collection, HttpStatus.BAD_REQUEST);
    	}
    	
    	collection.put("Description", "Not Authenticated.");
    	return new ResponseEntity<>(collection, HttpStatus.UNAUTHORIZED);
    }
    
    @GetMapping("/statistic/{AccountId}")
    public ResponseEntity<?> getStatistics(@PathVariable String AccountId, HttpServletRequest request, HttpServletResponse response){
    	HashMap<String, Integer> responseCollection = new HashMap<>();
    	HashMap<String, String> errorCollection = new HashMap<>();
    	Integer urlCount = 0;
		int initialCount = 1;
    	String longUrl = "";
    	String userName = "";
    	String userPart = "";
		String passwordPart = "";
		String[] headerParts;
    	
		// check to see if the Authorization header is set
    	if(request.getHeader("Authorization") != null) {
    		String requestHeaderValue = request.getHeader("Authorization").toString();
    		
    		// splitting the header to extract the key:value part
			headerParts = requestHeaderValue.split(":");
			
			// check to see if the header is set correctly
			if(headerParts.length != 1) {
    			userPart = headerParts[0];
        		passwordPart = headerParts[1];
			} else {
				errorCollection.put("Description", "Authorization header is not set correctly.");
	    		return new ResponseEntity<>(errorCollection, HttpStatus.BAD_REQUEST);
			}
			
			//check if user exists in our user database
			for(User user: userRepository.findByFirstName(AccountId)){
    			userName = user.getFirstName();
    			
    			// check if the password and username from the header are equal to the password and username in the database
            	if(!(user.getPassword().equals(passwordPart) && userName.equals(userPart))) {
            		errorCollection.put("Description", "Not Authenticated.");
            		return new ResponseEntity<>(errorCollection, HttpStatus.UNAUTHORIZED);
            	}
            }

			// getting registered links for the user we provided
    		for(URL url: urlRepository.findByFirstName(AccountId)){
            	longUrl = url.getLongUrl();
            	urlCount = url.getCount();
            	
            	// check if a link is registered multiple times
            	if(responseCollection.containsKey(longUrl)) {
            		initialCount += urlCount;
                    responseCollection.put(longUrl, initialCount);
            	} else {
                    responseCollection.put(longUrl, urlCount);
            	}
            }
    		
    		if(urlCount == 0) {
    			errorCollection.put("Description", "User hasn't registered any links.");
        		return new ResponseEntity<>(errorCollection, HttpStatus.OK);
    		}
    		return new ResponseEntity<>(responseCollection, HttpStatus.OK);
    	} else {
    		errorCollection.put("Description", "Authorization header is not set.");
    		return new ResponseEntity<>(errorCollection, HttpStatus.BAD_REQUEST);
    	}
    }
    
    @RequestMapping("/{shortUrl}")
    public RedirectView redirect(@PathVariable String shortUrl, HttpServletRequest request, HttpServletResponse response){
    	String redirectUrl = "";
    	String redirectShortUrl = "";
    	String username = "";
    	String password = "";
    	int redirectType = 302;
    	int count = 0;
    	int newCount = 0;
		String fullUrl = request.getRequestURL().toString(); // extracting the current url in the browser
    	RedirectView redirectView = new RedirectView();
    	
    	// checking to see if the currentUrl is found in our database. For example: https://localhost:8080/sjdja232
		for(URL url: urlRepository.findByShortUrl(fullUrl)){
			username = url.getFirstName();
			password = url.getPassword();
			redirectUrl = url.getLongUrl(); // getting the url where we should redirect
			redirectShortUrl = url.getShortUrl();
			count = url.getCount();
			redirectType = url.getRedirectType();
	    	newCount = count + 1; // increasing the counter of calling the url
	    	url.setCount(newCount); 
	    	try {
    	    	urlRepository.save(url); // saving the count in the database
	    	} catch(Exception e) {
	    		e.printStackTrace();
	            redirectView.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
	    	}
        }
				
        redirectView.setUrl(redirectUrl); // setting the url where we should redirect
        
        // check to see if the url is registered in our database
        if(redirectShortUrl == "") {
            redirectView.setStatusCode(HttpStatus.NOT_FOUND);
			return redirectView;
		}
        
        // checking the redirectType and redirecting with the correct http status code
        if(redirectType == 301) {
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        	return redirectView;
        } else {
            redirectView.setStatusCode(HttpStatus.FOUND);
        	return redirectView;
        }
    }
    
    @RequestMapping("/help")
    public String help() {
        return "help";
    }
    
}