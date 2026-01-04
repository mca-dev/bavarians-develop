package com.bavarians.controller;

import com.bavarians.dto.OfertySumaDto;
import com.bavarians.dto.Ponto;
import com.bavarians.graphql.model.Email;
import com.bavarians.graphql.model.Klient;
import com.bavarians.graphql.repository.EmailRepository;
import com.bavarians.service.OfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.bavarians.controller.PagesConstants.ADMIN_SZCZEGOLY_PAGE;
import static java.util.stream.Collectors.groupingBy;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private OfertaService ofertaService;
    @Autowired
    private EmailRepository emailRepository;


    @RequestMapping(value = "/plot", method = RequestMethod.GET)
    public String getDataPlot(ModelMap model) {
        Map<Integer, List<OfertySumaDto>> roczne = ofertaService.sumaMiesiecznyPrzychod().stream().collect(groupingBy(OfertySumaDto::getRok));
        roczne.forEach((r, dto) -> {
            ArrayList<Ponto> sumabezvat = new ArrayList<>();
            extractedSumabezvat(dto, sumabezvat);
            model.addAttribute("sumabezvat" + r, sumabezvat);

            ArrayList<Ponto> sumaczescibrutto = new ArrayList<>();
            extractedSumaczescibrutto(dto, sumaczescibrutto);
            model.addAttribute("sumaczescibrutto" + r, sumaczescibrutto);

            ArrayList<Ponto> sumarobociznanetto = new ArrayList<>();
            extractedSumarobociznanetto(dto, sumarobociznanetto);
            model.addAttribute("sumarobociznanetto" + r, sumarobociznanetto);

        });


        return ADMIN_SZCZEGOLY_PAGE;
    }

    private void extractedSumabezvat(List<OfertySumaDto> dto, List<Ponto> pontos) {
        dto.forEach(row -> {
            Ponto ponto = new Ponto();
            ponto.setX(row.getMiesiac());
            ponto.setY(row.getSumabezvat());
            pontos.add(ponto);
        });
    }

    private void extractedSumaczescibrutto(List<OfertySumaDto> dto, List<Ponto> pontos) {
        dto.forEach(row -> {
            Ponto ponto = new Ponto();
            ponto.setX(row.getMiesiac());
            ponto.setY(row.getSumaczescibrutto());
            pontos.add(ponto);
        });
    }

    private void extractedSumarobociznanetto(List<OfertySumaDto> dto, List<Ponto> pontos) {
        dto.forEach(row -> {
            Ponto ponto = new Ponto();
            ponto.setX(row.getMiesiac());
            ponto.setY(row.getSumarobociznanetto());
            pontos.add(ponto);
        });
    }

    @GetMapping("/emails")
    public String emaile(Model model) {
        model.addAttribute("emails", emailRepository.findAll());
        model.addAttribute("emails", emailRepository.findAll());
        model.addAttribute("settingsActive", true);

        return PagesConstants.EMAILS_PAGE;
    }

    @GetMapping("/email/dodaj")
    public String dodajEmaila(Model model) {
        model.addAttribute("email", new Email());
        return PagesConstants.EMAIL_NEW_TEMPLATE_PAGE;
    }

    @PostMapping("/email/dodaj")
    public String dodajEmaila(@ModelAttribute("email") Email email, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return PagesConstants.EMAIL_NEW_TEMPLATE_PAGE;
        }
        emailRepository.save(email);
        return "redirect:/" + "admin/emails";
    }

    @GetMapping("/email/usun/{id}")
    private String usunEmaila(@PathVariable("id") Long id) {
        emailRepository.deleteById(id);
        return "redirect:/" + "admin/emails";
    }

    @GetMapping("/email/edytuj/{id}")
    private String edytujEmaila(@PathVariable("id") Long id, Model model) {
        Optional<Email> email = emailRepository.findById(id);
        email.ifPresent(o -> {
            model.addAttribute("email", o);

        });
        return PagesConstants.EMAIL_EDIT_TEMPLATE_PAGE;
    }

    @PostMapping("/email/edytuj/{id}")
    public String edytujEmaila(@PathVariable("id") Long id, @ModelAttribute("email") Email emailForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return PagesConstants.EMAIL_EDIT_TEMPLATE_PAGE;
        }
        emailRepository.save(emailForm);
        return "redirect:/" + "admin/emails";
    }

//    SendMailBavarians sendMail = new SendMailBavarians();
//         sendMail.send("magdalena.ciezka@gmail.com");
}