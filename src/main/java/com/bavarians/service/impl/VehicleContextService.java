package com.bavarians.service.impl;

import com.bavarians.graphql.model.Element;
import com.bavarians.graphql.model.Oferta;
import com.bavarians.graphql.model.Pojazd;
import com.bavarians.graphql.repository.ElementRepository;
import com.bavarians.graphql.repository.OfertaRepository;
import com.bavarians.graphql.repository.PojazdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service for retrieving vehicle and service history context from database
 * to enhance Ollama chatbot responses (RAG - Retrieval-Augmented Generation)
 */
@Service
@Transactional(readOnly = true)
public class VehicleContextService {

    @Autowired
    private PojazdRepository pojazdRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private ElementRepository elementRepository;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Extract vehicle information from user message and retrieve context
     */
    public String getContextForMessage(String userMessage, Long vehicleId) {
        StringBuilder context = new StringBuilder();

        // If vehicleId is provided, get specific vehicle context
        if (vehicleId != null) {
            Pojazd vehicle = pojazdRepository.findById(vehicleId).orElse(null);
            if (vehicle != null) {
                context.append(formatVehicleContext(vehicle));
                context.append("\n\n");
                context.append(formatServiceHistory(vehicle));
            }
        } else {
            // Try to extract vehicle information from message
            List<Pojazd> matchingVehicles = findVehiclesFromMessage(userMessage);

            if (!matchingVehicles.isEmpty()) {
                context.append("ZNALEZIONE POJAZDY W BAZIE:\n");
                for (Pojazd vehicle : matchingVehicles) {
                    context.append(formatVehicleContext(vehicle));
                    context.append("\n");
                }
                context.append("\n");
            }
        }

        // Add common service types context
        context.append(getCommonServicesContext());

        return context.toString();
    }

    /**
     * Search for vehicles matching keywords in user message
     */
    private List<Pojazd> findVehiclesFromMessage(String message) {
        String normalizedMessage = message.toLowerCase();

        // Get all vehicles and filter by matching make/model/vin/plate
        return StreamSupport.stream(pojazdRepository.findAll().spliterator(), false)
            .filter(v -> {
                if (v.getMarka() != null && normalizedMessage.contains(v.getMarka().toLowerCase())) {
                    return true;
                }
                if (v.getModel() != null && normalizedMessage.contains(v.getModel().toLowerCase())) {
                    return true;
                }
                if (v.getVin() != null && normalizedMessage.contains(v.getVin().toLowerCase())) {
                    return true;
                }
                if (v.getNumerRejestracyjny() != null && normalizedMessage.contains(v.getNumerRejestracyjny().toLowerCase())) {
                    return true;
                }
                return false;
            })
            .limit(3) // Limit to top 3 matches
            .collect(Collectors.toList());
    }

    /**
     * Format vehicle information as context string
     */
    private String formatVehicleContext(Pojazd vehicle) {
        StringBuilder sb = new StringBuilder();
        sb.append("POJAZD:\n");
        sb.append("- ID: ").append(vehicle.getId()).append("\n");
        sb.append("- Marka: ").append(vehicle.getMarka()).append("\n");
        sb.append("- Model: ").append(vehicle.getModel()).append("\n");

        if (vehicle.getNumerRejestracyjny() != null) {
            sb.append("- Numer rejestracyjny: ").append(vehicle.getNumerRejestracyjny()).append("\n");
        }

        if (vehicle.getVin() != null) {
            sb.append("- VIN: ").append(vehicle.getVin()).append("\n");
        }

        if (vehicle.getPrzebieg() != null) {
            sb.append("- Przebieg: ").append(vehicle.getPrzebieg()).append(" km\n");
        }

        if (vehicle.getTerminOC() != null) {
            sb.append("- Termin OC: ").append(DATE_FORMAT.format(vehicle.getTerminOC())).append("\n");
        }

        if (vehicle.getTerminBadania() != null) {
            sb.append("- Termin badania: ").append(DATE_FORMAT.format(vehicle.getTerminBadania())).append("\n");
        }

        return sb.toString();
    }

    /**
     * Format service history for a vehicle
     */
    private String formatServiceHistory(Pojazd vehicle) {
        StringBuilder sb = new StringBuilder();
        // Fetch offers using repository to avoid lazy loading issues
        List<Oferta> offers = ofertaRepository.findAll().stream()
            .filter(o -> o.getPojazd() != null && o.getPojazd().getId().equals(vehicle.getId()))
            .collect(Collectors.toList());

        if (offers != null && !offers.isEmpty()) {
            sb.append("HISTORIA SERWISOWA (ostatnie usługi):\n");

            // Sort by date descending and take last 5
            List<Oferta> recentOffers = offers.stream()
                .sorted((o1, o2) -> o2.getEdytowano().compareTo(o1.getEdytowano()))
                .limit(5)
                .collect(Collectors.toList());

            for (Oferta offer : recentOffers) {
                sb.append("- Data: ").append(DATE_FORMAT.format(offer.getEdytowano()));
                sb.append(", Status: ").append(offer.getStatus());

                if (offer.getPrzebieg() != null) {
                    sb.append(", Przebieg: ").append(offer.getPrzebieg()).append(" km");
                }

                if (offer.getSuma() != null) {
                    sb.append(", Suma: ").append(offer.getSuma()).append(" PLN");
                }

                sb.append("\n");

                // Add service elements
                List<Element> elements = offer.getElementySerwisowe();
                if (elements != null && !elements.isEmpty()) {
                    for (Element element : elements) {
                        sb.append("  • ").append(element.getNazwa());

                        if (element.getCenaCzesciBrutto() != null && element.getCenaCzesciBrutto().compareTo(BigDecimal.ZERO) > 0) {
                            sb.append(" (części: ").append(element.getCenaCzesciBrutto()).append(" PLN)");
                        }

                        if (element.getCenaRobociznyNetto() != null && element.getCenaRobociznyNetto().compareTo(BigDecimal.ZERO) > 0) {
                            sb.append(" (robocizna: ").append(element.getCenaRobociznyNetto()).append(" PLN)");
                        }

                        sb.append("\n");
                    }
                }
            }
        } else {
            sb.append("HISTORIA SERWISOWA: Brak wcześniejszych serwisów dla tego pojazdu.\n");
        }

        return sb.toString();
    }

    /**
     * Get common service types and typical prices from database
     */
    private String getCommonServicesContext() {
        StringBuilder sb = new StringBuilder();
        sb.append("POPULARNE USŁUGI SERWISOWE:\n");

        // Get all elements and group by name to find most common services
        List<Element> allElements = (List<Element>) elementRepository.findAll();

        if (!allElements.isEmpty()) {
            // Count service types and get average prices
            var serviceStats = allElements.stream()
                .collect(Collectors.groupingBy(
                    Element::getNazwa,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            double avgPartPrice = list.stream()
                                .filter(e -> e.getCenaCzesciBrutto() != null)
                                .mapToDouble(e -> e.getCenaCzesciBrutto().doubleValue())
                                .average()
                                .orElse(0);

                            double avgLaborPrice = list.stream()
                                .filter(e -> e.getCenaRobociznyNetto() != null)
                                .mapToDouble(e -> e.getCenaRobociznyNetto().doubleValue())
                                .average()
                                .orElse(0);

                            return new ServiceStats(list.size(), avgPartPrice, avgLaborPrice);
                        }
                    )
                ));

            // Sort by frequency and take top 10
            serviceStats.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().count, e1.getValue().count))
                .limit(10)
                .forEach(entry -> {
                    String serviceName = entry.getKey();
                    ServiceStats stats = entry.getValue();

                    sb.append("- ").append(serviceName);
                    sb.append(" (wykonano: ").append(stats.count).append(" razy");

                    if (stats.avgPartPrice > 0) {
                        sb.append(", śr. cena części: ").append(String.format("%.2f", stats.avgPartPrice)).append(" PLN");
                    }

                    if (stats.avgLaborPrice > 0) {
                        sb.append(", śr. robocizna: ").append(String.format("%.2f", stats.avgLaborPrice)).append(" PLN");
                    }

                    sb.append(")\n");
                });
        } else {
            sb.append("Brak danych o wcześniejszych usługach w systemie.\n");
        }

        return sb.toString();
    }

    /**
     * Helper class for service statistics
     */
    private static class ServiceStats {
        int count;
        double avgPartPrice;
        double avgLaborPrice;

        public ServiceStats(int count, double avgPartPrice, double avgLaborPrice) {
            this.count = count;
            this.avgPartPrice = avgPartPrice;
            this.avgLaborPrice = avgLaborPrice;
        }
    }
}
