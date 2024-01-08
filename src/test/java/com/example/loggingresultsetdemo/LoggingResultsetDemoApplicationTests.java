package com.example.loggingresultsetdemo;

import com.example.loggingresultsetdemo.domain.Author;
import com.example.loggingresultsetdemo.domain.AuthorRepository;
import com.example.loggingresultsetdemo.domain.Book;
import com.example.loggingresultsetdemo.domain.BookRepository;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@Import(TestLoggingResultsetDemoApplication.class)
class LoggingResultsetDemoApplicationTests {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void contextLoads() {
        Author johnDoe = authorRepository.save(new Author("John Doe"));
        Author johnDoe2 = authorRepository.save(new Author("John Doe2"));

        bookRepository.save(new Book("book1", johnDoe));
        bookRepository.save(new Book("book2", johnDoe));
        bookRepository.save(new Book("book3", johnDoe));
        bookRepository.save(new Book("book4", johnDoe));
        bookRepository.save(new Book("book5", johnDoe2));

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            for (Book book : bookRepository.findAll()) {
                System.out.println("title: '" + book.getTitle());
                System.out.println("title: '" + book.getTitle() + "', author: '" + book.getAuthor().getName() + "'");
            }
        });
    }

}
