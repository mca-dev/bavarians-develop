package com.bavarians.controller;

import com.bavarians.graphql.model.Klient;
import com.bavarians.graphql.repository.ElementRepository;
import com.bavarians.graphql.repository.KlientRepository;
import com.bavarians.graphql.repository.PojazdRepository;
import com.bavarians.service.OfertaService;
import com.bavarians.service.impl.KlientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("klienci")
public class KlientController {
    public static final String REDIRECT_KLIENCI = "redirect:/klienci";
    @Autowired
    private KlientService userService;
    @Autowired
    private OfertaService ofertaService;
    @Autowired
    private PojazdRepository pojazdRepository;
    @Autowired
    private ElementRepository elementRepository;
    @Autowired
    private KlientRepository klientRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @GetMapping
    public String klienci(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("klienciActive", true);

        return PagesConstants.KLIENCI_PAGE;
    }
    @GetMapping("/dodaj")
    public String dodajKlienta(Model model) {
        model.addAttribute("userForm", new Klient());
        return PagesConstants.NOWY_KLIENT_PAGE;
    }

    @PostMapping("/dodaj")
    public String dodajKlienta(@ModelAttribute("userForm") Klient userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return PagesConstants.NOWY_KLIENT_PAGE;
        }
        userService.save(userForm);
        return REDIRECT_KLIENCI;
    }

    @GetMapping("/usun/{id}")
    private String usunKlienta(@PathVariable("id") Long id) {
        klientRepository.deleteById(id);
        return "redirect:/klienci/";
    }

    @GetMapping("/edytuj/{id}")
    private String edytujKlienta(@PathVariable("id") Long id, Model model) {
        Optional<Klient> pojazdOptional = klientRepository.findById(id);
        pojazdOptional.ifPresent(o -> {
            model.addAttribute("klientForm", o);

        });
        return PagesConstants.EDYCJA_KLIENT_PAGE;
    }

    @PostMapping("/edytuj/{id}")
    public String edytujKlienta(@PathVariable("id") Long id, @ModelAttribute("klientForm") Klient klientForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return PagesConstants.EDYCJA_KLIENT_PAGE;
        }
        klientForm.setHaslo(bCryptPasswordEncoder.encode(klientForm.getHaslo()));
        klientRepository.save(klientForm);
        return REDIRECT_KLIENCI;
    }
}
