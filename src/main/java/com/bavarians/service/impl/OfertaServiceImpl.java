package com.bavarians.service.impl;

import com.bavarians.dto.OfertySumaDto;
import com.bavarians.graphql.model.Element;
import com.bavarians.graphql.model.Oferta;
import com.bavarians.graphql.model.Pojazd;
import com.bavarians.graphql.repository.ElementRepository;
import com.bavarians.graphql.repository.OfertaRepository;
import com.bavarians.graphql.repository.PojazdRepository;
import com.bavarians.service.OfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OfertaServiceImpl implements OfertaService {
    @Autowired
    OfertaRepository ofertaRepository;
    @Autowired
    private ElementRepository elementRepository;
    @Autowired
    private KlientService klientService;
    @Autowired
    private PojazdRepository pojazdRepository;

    @Override
    public Oferta recalculateAndSave(Oferta o) {
        return przeliczCeneOferty(o, o.getElementySerwisowe());
    }

    @Override
    public Oferta recalculateAndSave(Oferta o, List<Element> elementySerwisowe) {
        return przeliczCeneOferty(o, elementySerwisowe);
    }

    @Override
    public Oferta findByIdWithElements(Long id) {
        return ofertaRepository.findOneWithPojazdAndElements2(id);
    }

    @Override
    public Optional<Oferta> findById(Long id) {
        return ofertaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        ofertaRepository.findById(id).ifPresent(it -> {
            elementRepository.deleteAll(it.getElementySerwisowe());
        });
        ofertaRepository.deleteById(id);
    }

    @Override
    public List<OfertySumaDto> sumaMiesiecznyPrzychod() {
        return ofertaRepository.sumaMiesiecznyPrzychod();
    }

    @Override
    public List<Oferta> findAll() {
        return ofertaRepository.findAll(Sort.by(Sort.Direction.DESC, "edytowano"));
    }

    @Override
    public List<Oferta> findAllByWlasciciel() {
        return ofertaRepository.findAllByWlasciciel(klientService.getCurrentKlient());
    }

    @Override
    public void deleteAll(List<Oferta> oferty) {
        ofertaRepository.deleteAll(oferty);
    }

    private Oferta przeliczCeneOferty(Oferta oferta, List<Element> elementy) {
        BigDecimal czesciBrutto = elementy.stream()
                .map(Element::getCenaCzesciBrutto)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal robociznaNetto = elementy.stream()
                .map(Element::getCenaRobociznyNetto)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        oferta.setSumaCzesciBrutto(czesciBrutto);
        oferta.setSumaRobociznaNetto(robociznaNetto);
        oferta.setSuma(czesciBrutto.add(robociznaNetto.multiply(BigDecimal.valueOf(1.23))));
        oferta.setSumaBezVat(czesciBrutto.add(robociznaNetto));

        Oferta save = ofertaRepository.save(oferta);

        savePojazdWlasciciel(save);
        return save;
    }

    private void savePojazdWlasciciel(Oferta save) {
        Pojazd pojazd = save.getPojazd();
        pojazd.setWlasciciel(klientService.getCurrentKlient());
        pojazdRepository.save(pojazd);
    }
}
