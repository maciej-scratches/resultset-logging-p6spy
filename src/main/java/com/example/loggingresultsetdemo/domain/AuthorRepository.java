package com.example.loggingresultsetdemo.domain;

import org.springframework.data.repository.ListCrudRepository;

public interface AuthorRepository extends ListCrudRepository<Author, String> {
}
