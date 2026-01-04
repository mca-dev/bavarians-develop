package com.bavarians.service;

import com.bavarians.dto.OfertySumaDto;
import com.bavarians.graphql.model.Element;
import com.bavarians.graphql.model.Oferta;

import java.util.List;
import java.util.Optional;

public interface OfertaService {
    Oferta recalculateAndSave(Oferta o);

    Oferta recalculateAndSave(Oferta o, List<Element> elementySerwisowe);

    Oferta findByIdWithElements(Long id);

    Optional<Oferta> findById(Long id);

    void deleteById(Long id);

    List<OfertySumaDto> sumaMiesiecznyPrzychod();

    List<Oferta> findAll();

    List<Oferta> findAllByWlasciciel();

    void deleteAll(List<Oferta> oferty);
}
