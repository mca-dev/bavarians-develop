package com.bavarians.graphql.repository;

import com.bavarians.graphql.model.Email;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends PagingAndSortingRepository<Email, Long> {

    Email findByUsername(String username);

}