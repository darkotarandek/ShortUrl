package com.urlshortener.UrlShortener.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.urlshortener.UrlShortener.model.User;

public interface UserRepository extends CrudRepository<User, Long>{
    List<User> findByFirstName(String firstName);
} 