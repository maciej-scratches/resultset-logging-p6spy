package com.example.loggingresultsetdemo.domain;

import org.springframework.data.repository.ListCrudRepository;

public interface BookRepository extends ListCrudRepository<Book, String> {
}
