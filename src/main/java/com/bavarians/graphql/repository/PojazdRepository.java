package com.bavarians.graphql.repository;

import com.bavarians.graphql.model.Klient;
import com.bavarians.graphql.model.Oferta;
import com.bavarians.graphql.model.Pojazd;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PojazdRepository extends PagingAndSortingRepository<Pojazd, Long> {

    @Query("from pojazd p left join klient k on p.wlasciciel=k.id where p.wlasciciel =?1")
    List<Pojazd> findAllByWlasciciel(Klient id);
}