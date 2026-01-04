package com.bavarians.controller;

import com.bavarians.graphql.model.*;
import com.bavarians.graphql.repository.ElementRepository;
import com.bavarians.graphql.repository.KlientRepository;
import com.bavarians.graphql.repository.OfertaRepository;
import com.bavarians.graphql.repository.PojazdRepository;
import com.bavarians.service.OfertaService;
import com.bavarians.service.impl.KlientService;
import com.bavarians.service.impl.SendMailBavarians;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("pojazdy")
public class PojazdController {
    public static final String REDIRECT_POJAZDY = "redirect:/pojazdy/";
    @Autowired
    private KlientService userService;
    @Autowired
    private OfertaService ofertaService;
    @Autowired
    private OfertaRepository ofertaRepository;
    @Autowired
    private PojazdRepository pojazdRepository;
    @Autowired
    private ElementRepository elementRepository;
    @Autowired
    private KlientRepository klientRepository;
    @Autowired
    private KlientService klientService;
    @Autowired
    private SendMailBavarians sendMailBavarians;

    @GetMapping
    public String pojazdy(Model model) {
        Klient currentKlient = klientService.getCurrentKlient();
        Set<Role> roles = currentKlient.getRoles();
        if (roles.stream().map(Role::getName).anyMatch(it -> it.equals("ROLE_ADMIN"))) {
            model.addAttribute("pojazdy", pojazdRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        } else {
            model.addAttribute("pojazdy", pojazdRepository.findAllByWlasciciel(currentKlient));
        }
        model.addAttribute("pojazdyActive", true);

        return PagesConstants.POJAZDY_PAGE;
    }

    @GetMapping("/{id}")
    public String pojazdSzczegoly(@PathVariable String id, Model model) {
        Optional<Pojazd> pojazdOptional = pojazdRepository.findById(Long.valueOf(id));
        pojazdOptional.ifPresent(pojazd -> model.addAttribute("pojazd", pojazd));
        return PagesConstants.POJAZD_SZCZEGOLY_PAGE;
    }

    @GetMapping("/usun/{id}")
    private String usunPojazd(@PathVariable("id") Long id) {
        pojazdRepository.findById(id).ifPresent(pojazd -> {
            List<Oferta> oferty = pojazd.getOferty();
            oferty.forEach(o -> {
                elementRepository.deleteAll(o.getElementySerwisowe());
            });
        });
        pojazdRepository.findById(id).ifPresent(it -> {
            ofertaService.deleteAll(it.getOferty());
        });
        pojazdRepository.deleteById(id);
        return REDIRECT_POJAZDY;
    }

    @GetMapping("/dodaj")
    private String dodajPojazd(Model model) {
        model.addAttribute("pojazdForm", new Pojazd());
        return PagesConstants.NOWY_POJAZD_PAGE;
    }

    @PostMapping("/dodaj")
    public String dodajPojazd(@ModelAttribute("pojazdForm") Pojazd pojazdForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return PagesConstants.NOWY_POJAZD_PAGE;
        }
        Klient currentKlient = klientService.getCurrentKlient();
        pojazdForm.setWlasciciel(currentKlient);
        pojazdRepository.save(pojazdForm);
        return REDIRECT_POJAZDY;
    }


    @GetMapping("/edytuj/{id}")
    private String edytujPojazd(@PathVariable("id") Long id, Model model) {
        Optional<Pojazd> pojazdOptional = pojazdRepository.findById(id);
        pojazdOptional.ifPresent(o -> {
            model.addAttribute("pojazdForm", o);

        });
        return PagesConstants.EDYCJA_POJAZD_PAGE;
    }

    @PostMapping("/edytuj/{id}")
    public String edytujPojazd(@PathVariable("id") Long id, @ModelAttribute("pojazdForm") Pojazd pojazdForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return PagesConstants.EDYCJA_POJAZD_PAGE;
        }
        pojazdRepository.save(pojazdForm);
        return REDIRECT_POJAZDY;
    }

    @PostMapping("/aktywuj-powiadomienia/{id}")
    public String powiadomieniaPojazd(@PathVariable("id") Long id, @ModelAttribute("pojazdForm") Pojazd pojazdForm,
                                      BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return PagesConstants.EDYCJA_POJAZD_PAGE;
        }
        Optional<Pojazd> pojazdOptional = pojazdRepository.findById(id);
        pojazdOptional.ifPresent(o -> {
            o.setAktywnePowiadomienia(true);
            pojazdRepository.save(o);

        });
        return REDIRECT_POJAZDY;
    }


    @GetMapping("/nowa-oferta/{pojazdId}")
    private String utworzOferteDlaPojazdu(@PathVariable("pojazdId") Long id, Model model) {
        Optional<Oferta> optionalOferta = pojazdRepository.findById(id).map(pojazd -> {
            Oferta oferta = new Oferta();
            if (oferta.getEdytowano() == null) {
                oferta.setEdytowano(new Date());
            }
            oferta.setPojazd(pojazd);
            ofertaService.recalculateAndSave(oferta);
            Element element = new Element();
            element.setNazwa("");
            element.setOferta(oferta);
            oferta.setElementySerwisowe(List.of(element));
            elementRepository.save(element);
            ofertaRepository.flush();

            model.addAttribute("ofertaForm", oferta);
            return oferta;
        });

        return PagesConstants.EDYCJA_OFERTA_PAGE;
    }

}