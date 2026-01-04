package com.bavarians.controller;

import com.bavarians.dto.OfertySumaDto;
import com.bavarians.dto.Ponto;
import com.bavarians.service.OfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bavarians.controller.PagesConstants.ADMIN_SZCZEGOLY_PAGE;
import static java.util.stream.Collectors.groupingBy;

@Controller
@RequestMapping("/")
public class FinancialController {
    @Autowired
    private OfertaService ofertaService;

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
}