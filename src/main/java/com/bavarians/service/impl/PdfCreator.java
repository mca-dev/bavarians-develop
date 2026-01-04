package com.bavarians.service.impl;

import com.bavarians.graphql.model.Oferta;
import com.bavarians.graphql.model.Pojazd;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Stream;

import static com.itextpdf.text.Element.ALIGN_MIDDLE;

@Service
public class PdfCreator {
    private static final Logger logger = LoggerFactory.getLogger(PdfCreator.class);

    public static final int FIXED_HEIGHT = 30;
    public static final String STATIC_IMG_BAVARIANS_LOGO_PNG_LOCAL = "target/classes/static/img/bavarians-logo.png";
    public static final String STATIC_IMG_BAVARIANS_LOGO_PNG = "BOOT-INF/classes/static/img/bavarians-logo.png";
    public static final String BAVARIANS_ADRIAN_DABROWSKI = "Bavarians Adrian Dąbrowski";
    public static final String UL_NOWOWIEJSKA_88 = "ul. Nowowiejska 88";
    public static final String POGROSZEW_KOLONIA = "05-850 Pogroszew-Kolonia";
    public static final String NIP_1182073903 = "NIP: 1182073903";
    public static final String TEL = "TEL.: 531-276-707";
    public static final BaseColor LIGHT_GREY = new BaseColor(236, 236, 236);
    private Font basefont;
    private Font fontHeader;
    private Font basefontBold;

    public void createOfferPdf(String filename, Oferta oferta) throws IOException, DocumentException, URISyntaxException {
        BaseFont baseFont = BaseFont.createFont("static/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        basefont = new Font(baseFont, 11);
        basefontBold = new Font(baseFont, 11);
        basefontBold.setStyle(1);
        fontHeader = new Font(baseFont, 18);
        fontHeader.setStyle(1);
        Document document = new Document();
        FileOutputStream fileOutputStream = new FileOutputStream(filename);
        PdfWriter.getInstance(document, fileOutputStream);
        document.open();
        LocalDate edytowano = oferta.getEdytowano().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        document.add(getParagraphRightAlignment(createChunkWithBaseFont(getDate(edytowano), basefont)));

        try {
            document.add(getLogo());
        } catch (IOException e) {
            logger.error("PDF logo failed", e);
        }

        document.add(getParagraphRightAlignment(createChunkWithBaseFont(BAVARIANS_ADRIAN_DABROWSKI, basefontBold)));
        document.add(getParagraphRightAlignment(createChunkWithBaseFont(UL_NOWOWIEJSKA_88, basefont)));
        document.add(getParagraphRightAlignment(createChunkWithBaseFont(POGROSZEW_KOLONIA, basefont)));
        document.add(getParagraphRightAlignment(createChunkWithBaseFont(NIP_1182073903, basefont)));
        document.add(getParagraphRightAlignment(createChunkWithBaseFont(TEL, basefont)));

        document.add(getParagraphCenterAlignment(createChunkWithBaseFont("Kosztorys naprawy", fontHeader)));
        document.add(getParagraphLeftAlignment(createChunkWithBaseFont("DANE POJAZDU", basefontBold)));
        document.add(getParagraphLeftAlignment(createChunkWithBaseFont(getPojazdInfo(oferta), basefont)));
        document.add(getParagraphLeftAlignment(createChunkWithBaseFont(getPojazdNrRej(oferta), basefont)));
        document.add(getParagraphLeftAlignment(createChunkWithBaseFont(getPojazdVin(oferta), basefont)));
        document.add(getParagraphLeftAlignment(createChunkWithBaseFont(getPojazdPrzebieg(oferta), basefont)));

        PdfPTable table = new PdfPTable(new float[]{60, 20, 20});

        table.setSpacingBefore(50);
        table.setSpacingAfter(50);
        table.setWidthPercentage(100);
        addTableHeader(table);
        oferta.getElementySerwisowe().forEach(element -> addRows(table, element));
        dodajPodsumowanieOferty(table, oferta);
        document.add(table);

        document.add(getParagraphRightAlignment(createChunkWithBaseFont("Suma netto " + getFormattedCena(oferta.getSumaBezVat()), basefont)));
        document.add(getParagraphRightAlignment(createChunkWithBaseFont("Suma brutto " + getFormattedCena(oferta.getSuma()), basefont)));

        document.close();
    }

    @NotNull
    private Paragraph getParagraphLeftAlignment(Chunk chunk) {
        return new Paragraph(chunk);
    }

    @NotNull
    private Chunk createChunkWithBaseFont(String text, Font font) {
        return createChunkWithFont(text, font);
    }

    @NotNull
    private Chunk createChunkWithFont(String text, Font font) {
        return new Chunk(text, font);
    }

    @NotNull
    private Phrase createPhrase(String columnTitle, Font basefont) {
        return new Phrase(createChunkWithBaseFont(columnTitle, basefont));
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("WYKAZ CZYNNOŚCI I ELEMENTÓW", "ROBOCIZNA", "CZĘŚCI")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setFixedHeight(FIXED_HEIGHT + 10);
                    header.setBackgroundColor(LIGHT_GREY);
                    header.setBorder(0);
                    //   header.setBorderWidthBottom(1);
                    //   header.setBorderWidthTop(1);
                    header.setVerticalAlignment(ALIGN_MIDDLE);
                    //    header.setBorderColorBottom(new BaseColor(220, 20, 60));
                    header.setPhrase(createPhrase(columnTitle, basefontBold));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, com.bavarians.graphql.model.Element element) {
        table.addCell(getStandardCell(element.getNazwa()));
        table.addCell(getStandardCell(getFormattedCena(element.getCenaRobociznyNetto())));
        table.addCell(getStandardCell(getFormattedCena(element.getCenaCzesciBrutto())));
    }

    @NotNull
    private PdfPCell getStandardCell(String text) {
        PdfPCell pdfPCell = new PdfPCell(createPhrase(text, basefont));
        pdfPCell.setBorder(0);
        pdfPCell.setBorderWidthTop(1);
        pdfPCell.setBorderColorTop(BaseColor.LIGHT_GRAY);
        pdfPCell.setFixedHeight(FIXED_HEIGHT);
        pdfPCell.setVerticalAlignment(ALIGN_MIDDLE);
        return pdfPCell;
    }

    private void dodajPodsumowanieOferty(PdfPTable table, Oferta oferta) {
        table.addCell(createPdfCell(null));
        table.addCell(createPdfCell(oferta.getSumaRobociznaNetto()));
        table.addCell(createPdfCell(oferta.getSumaCzesciBrutto()));
    }

    @NotNull
    private PdfPCell createPdfCell(BigDecimal bigDecimal) {
        PdfPCell cell = getStandardCell(getFormattedCena(bigDecimal));
        cell.setBorder(0);
        cell.setBorderWidthTop(1);
        cell.setPaddingTop(5);
        cell.setFixedHeight(FIXED_HEIGHT);
        cell.setBorderColorTop(new BaseColor(220, 20, 60));
        return cell;
    }

    private String getFormattedCena(BigDecimal val) {
        return String.format("%s", Optional.ofNullable(val).map(d -> d + " zł").orElse(""));
    }

    private String getPojazdInfo(Oferta oferta) {
        return getPojazd(oferta).map(pojazd -> "Marka/model: " + pojazd.getMarka() + " " + pojazd.getModel()).orElse("");
    }

    private String getPojazdNrRej(Oferta oferta) {
        return getPojazd(oferta).map(pojazd -> "Numer rejestracyjny: " + pojazd.getNumerRejestracyjny()).orElse("");
    }

    private Optional<Pojazd> getPojazd(Oferta oferta) {
        return Optional.ofNullable(oferta.getPojazd());
    }

    private String getPojazdVin(Oferta oferta) {
        return getPojazd(oferta).map(pojazd -> "VIN: " + pojazd.getVin()).orElse("");
    }

    private String getPojazdPrzebieg(Oferta oferta) {
        String przebieg = "";
        if (StringUtils.isNotEmpty(oferta.getPrzebieg())) {
            przebieg = oferta.getPrzebieg();
        } else {
            przebieg = getPojazd(oferta)
                    .map(Pojazd::getPrzebieg)
                    .orElse("");
        }
        return "Przebieg: " + przebieg;
    }

    @NotNull
    private Paragraph getParagraphRightAlignment(Chunk date) {
        Paragraph e1 = getParagraphLeftAlignment(date);
        e1.setAlignment(Element.ALIGN_RIGHT);
        return e1;
    }

    @NotNull
    private Paragraph getParagraphCenterAlignment(Chunk chunk) {
        Paragraph paragraph = getParagraphLeftAlignment(chunk);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(30);
        paragraph.setSpacingBefore(20);
        return paragraph;
    }

    @NotNull
    private String getDate(LocalDate now) {
        LocalDate date = now;
        return "Data: " + date;
    }

    private Image getLogo() throws BadElementException, IOException {
        Image instance;
        ClassPathResource classPathResource = new ClassPathResource("static/img/bavarians-logo.png");
        InputStream inputStream = classPathResource.getInputStream();
        logger.info("logo bavarians inputStream" + inputStream);

        instance = Image.getInstance(inputStream.readAllBytes());
        return instance;
    }
}

