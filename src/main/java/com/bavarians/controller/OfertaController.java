package com.bavarians.controller;

import com.bavarians.graphql.model.*;
import com.bavarians.graphql.repository.ElementRepository;
import com.bavarians.graphql.repository.KlientRepository;
import com.bavarians.graphql.repository.PojazdRepository;
import com.bavarians.service.OfertaService;
import com.bavarians.service.impl.DefaultUserDetailsService;
import com.bavarians.service.impl.KlientService;
import com.bavarians.service.impl.PdfCreator;
import com.itextpdf.text.DocumentException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;

import static java.lang.String.format;

@Controller
@RequestMapping("oferty")
public class OfertaController {

    @Autowired
    private OfertaService ofertaService;
    @Autowired
    private PojazdRepository pojazdRepository;
    @Autowired
    private ElementRepository elementRepository;
    @Autowired
    private PdfCreator pdfCreator;
    @Autowired
    private DefaultUserDetailsService userDetailsService;
    @Autowired
    private KlientRepository klientRepository;
    @Autowired
    private KlientService klientService;

    @GetMapping
    public String oferty(Model model) {
        Set<Role> roles = klientService.getCurrentKlient().getRoles();
        if (roles.stream().map(Role::getName).anyMatch(it -> it.equals("ROLE_ADMIN"))) {
            model.addAttribute("oferty", ofertaService.findAll());
        } else {
            model.addAttribute("oferty", ofertaService.findAllByWlasciciel());
        }
        model.addAttribute("ofertyActive", true);

        return PagesConstants.OFERTY_PAGE;
    }


    @GetMapping("/{id}")
    public String ofertySzczegoly(@PathVariable String id, Model model) {
        Optional<Oferta> ofertaOptional = ofertaService.findById(Long.valueOf(id));
        ofertaOptional.ifPresent(o -> model.addAttribute("oferta", o));
        return PagesConstants.OFERTY_SZCZEGOLY_PAGE;
    }

    @GetMapping("/dodaj")
    public String nowaOferta(Model model) {
        Oferta o = new Oferta();
        model.addAttribute("ofertaForm", o);
        return PagesConstants.NOWA_OFERTA_PAGE;
    }

    @PostMapping("/dodaj")
    public String nowaOferta(@ModelAttribute("ofertaForm") Oferta ofertaForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return PagesConstants.NOWA_OFERTA_PAGE;
        }
        if (ofertaForm.getEdytowano() == null) {
            ofertaForm.setEdytowano(new Date());
        }
        Pojazd pojazd = ofertaForm.getPojazd();
        if (pojazd != null) {
            if (StringUtils.join(pojazd.getMarka(), pojazd.getModel(), pojazd.getNumerRejestracyjny(), pojazd.getPrzebieg(), pojazd.getVin()).isEmpty()) {
                ofertaForm.setPojazd(null);
            } else {
                pojazdRepository.save(pojazd);
            }
        }
        ArrayList<Element> elementySerwisoweToSave = new ArrayList<>();
        List<Element> elementySerwisowe = ofertaForm.getElementySerwisowe();
        for (Element e : elementySerwisowe) {
            if (StringUtils.isNotBlank(e.getNazwa())) {
                e.setOferta(ofertaForm);
                elementySerwisoweToSave.add(e);
            }
        }
        ofertaForm.setElementySerwisowe(elementySerwisoweToSave);

        Oferta oferta = ofertaService.recalculateAndSave(ofertaForm, elementySerwisoweToSave);

        elementRepository.saveAll(elementySerwisoweToSave);
        return "redirect:/oferty/" + oferta.getId();
    }


    @GetMapping("/{ofertaId}/elementy/usun/{id}")
    private String usunElement(@PathVariable("id") Long id, @PathVariable("ofertaId") Long ofertaId) {
        elementRepository.deleteById(id);
        Optional<Oferta> oferta = ofertaService.findById(ofertaId);
        oferta.ifPresent(o -> ofertaService.recalculateAndSave(o));
        return "redirect:/oferty/" + ofertaId;
    }

    @GetMapping("/{ofertaId}/edycja/elementy/usun/{id}")
    private String usunElementPodczasEdycji(@PathVariable("id") Long id, @PathVariable("ofertaId") Long ofertaId) {
        elementRepository.deleteById(id);
        Optional<Oferta> oferta = ofertaService.findById(ofertaId);
        oferta.ifPresent(o -> ofertaService.recalculateAndSave(o));
        return "redirect:/oferty/edytuj/" + ofertaId;
    }

    @GetMapping("/usun/{id}")
    private String usunOferte(@PathVariable("id") Long id) {
        ofertaService.deleteById(id);
        return "redirect:/oferty/";
    }

    @GetMapping("/edytuj/{id}")
    private String edytujOferte(@PathVariable("id") Long id, Model model) {
        Optional<Oferta> oneWithPojazd = ofertaService.findById(id);

        oneWithPojazd.ifPresent(o -> {
            model.addAttribute("ofertaForm", o);

        });
        return PagesConstants.EDYCJA_OFERTA_PAGE;
    }

    @PostMapping("/edytuj/{id}")
    public String edytujOferte(@PathVariable("id") Long id, @ModelAttribute("ofertaForm") Oferta ofertaForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return PagesConstants.EDYCJA_OFERTA_PAGE;
        }
        Pojazd pojazd = ofertaForm.getPojazd();
        if (pojazd != null) {
            if (StringUtils.join(pojazd.getMarka(), pojazd.getModel(), pojazd.getNumerRejestracyjny(), pojazd.getPrzebieg(), pojazd.getVin()).isEmpty()) {
                ofertaForm.setPojazd(null);
            } else {
                pojazdRepository.save(pojazd);
            }
        }
        Optional<Oferta> ofertaOptional = ofertaService.findById(Long.valueOf(id));
        ofertaOptional.ifPresent(o -> {
            List<Element> elementySerwisowe = ofertaForm.getElementySerwisowe();
            if (elementySerwisowe.size() > 0) {
                elementRepository.deleteAll(o.getElementySerwisowe());

                elementySerwisowe.forEach(ee -> {
                    if (ee.getNazwa() == null) {
                        System.out.println("OfertaController ERROR MCA element nazwa NULL");
                    }
                    if (StringUtils.isNotBlank(ee.getNazwa())) {
                        ee.setOferta(ofertaForm);
                        elementRepository.save(ee);
                    }
                });
                // elementRepository.saveAll(elementySerwisowe);
                //o.setElementySerwisowe(elementySerwisowe);
                //ofertaForm.setEdytowano(o.getEdytowano());
                 ofertaService.recalculateAndSave(ofertaForm, elementySerwisowe);

            }

        });


        return "redirect:/oferty/" + id;
    }

    @GetMapping("/pdf/{id}")
    private void generujPdf(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        Optional<Oferta> oneWithPojazd = ofertaService.findById(id);
        try {
            if (oneWithPojazd.isPresent()) {
                String filename = "/tmp/" + oneWithPojazd.map(this::getFilename).orElse("oferta-" + id);
                pdfCreator.createOfferPdf(filename, oneWithPojazd.get());
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
                InputStream inputStream = new FileInputStream(filename);
                int nRead;
                while ((nRead = inputStream.read()) != -1) {
                    response.getWriter().write(nRead);
                }
                File file = new File(filename);
                boolean delete = file.delete();
                System.out.println(delete);
            }

        } catch (DocumentException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private String getFilename(Oferta oferta) {
        return Optional.ofNullable(oferta.getPojazd()).map(p -> format("%s-%s-%s.pdf", p.getMarka(), p.getModel(), oferta.getId())
                        .replaceAll("\\s+", "_"))
                .orElse(format("oferta-%s.pdf", oferta.getId()));

    }

    @GetMapping("/email/{id}")
    private void wyslijEmail(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        Optional<Oferta> oferta = ofertaService.findById(id);
        try {
            if (oferta.isPresent()) {
                String filename =  "/tmp/" + oferta.map(this::getFilename).orElse("oferta-" + id);
                pdfCreator.createOfferPdf(filename, oferta.get());
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
                InputStream inputStream = new FileInputStream(filename);
                int nRead;
                while ((nRead = inputStream.read()) != -1) {
                    response.getWriter().write(nRead);
                }
                File file = new File(filename);
                boolean delete = file.delete();
                System.out.println(delete);
            }

        } catch (DocumentException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}