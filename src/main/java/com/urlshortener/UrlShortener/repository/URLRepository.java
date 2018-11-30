package com.urlshortener.UrlShortener.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.urlshortener.UrlShortener.model.URL;

public interface URLRepository extends CrudRepository<URL, Long>{
    List<URL> findByFirstName(String firstName);
    List<URL> findByShortUrl(String shortUrl);
} 