package com.bavarians.graphql.repository;

import com.bavarians.graphql.model.Klient;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KlientRepository extends PagingAndSortingRepository<Klient, Long> {

    Klient findByEmail(String username);

}