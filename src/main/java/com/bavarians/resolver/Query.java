package com.bavarians.resolver;

import com.bavarians.graphql.model.Oferta;
import com.bavarians.graphql.repository.OfertaRepository;
import com.bavarians.graphql.repository.PojazdRepository;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class Query implements GraphQLQueryResolver {
    private final OfertaRepository ofertaRepository;
    private final PojazdRepository pojazdRepository;

    @Autowired
    public Query(OfertaRepository ofertaRepository, PojazdRepository pojazdRepository) {
        this.ofertaRepository = ofertaRepository;
        this.pojazdRepository = pojazdRepository;
    }

    public Iterable<Oferta> findAllVehicleRepairs() {
        return ofertaRepository.findAllWithPojazdAndElements().stream().distinct()
                .collect(Collectors.toList());
    }
}
