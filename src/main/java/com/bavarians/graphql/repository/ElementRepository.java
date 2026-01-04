package com.bavarians.graphql.repository;

import com.bavarians.graphql.model.Element;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElementRepository extends PagingAndSortingRepository<Element, Long> {

    //Klient findByEmail(String username);

}