package com.bavarians.graphql.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "element")
@Table(name = "element_serwisowy")
public class Element {
    @Id
    @SequenceGenerator(name="seq",sequenceName="elem_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
    private Long id;

    @Column(name = "nazwa", nullable = false)
    private String nazwa;

    @Column(name = "cenaCzesciBrutto")
    private BigDecimal cenaCzesciBrutto;

    @Column(name = "cenaRobociznyNetto")
    private BigDecimal cenaRobociznyNetto;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oferta_id")
    private Oferta oferta;

    public Element() {
    }

    public Element(String nazwa, BigDecimal cenaCzesciBrutto, BigDecimal cenaRobociznyNetto) {
        this.nazwa = nazwa;
        this.cenaCzesciBrutto = cenaCzesciBrutto;
        this.cenaRobociznyNetto = cenaRobociznyNetto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public BigDecimal getCenaCzesciBrutto() {
        return cenaCzesciBrutto;
    }

    public void setCenaCzesciBrutto(BigDecimal cenaCzesciBrutto) {
        this.cenaCzesciBrutto = cenaCzesciBrutto;
    }

    public BigDecimal getCenaRobociznyNetto() {
        return cenaRobociznyNetto;
    }

    public void setCenaRobociznyNetto(BigDecimal cenaRobociznyNetto) {
        this.cenaRobociznyNetto = cenaRobociznyNetto;
    }

    public Oferta getOferta() {
        return oferta;
    }

    public void setOferta(Oferta oferta) {
        this.oferta = oferta;
    }

    @Override
    public String toString() {
        return "Element{" +
                "nazwa='" + nazwa + '\'' +
                ", cenaCzesciBrutto='" + cenaCzesciBrutto + '\'' +
                ", cenaRobociznyNetto='" + cenaRobociznyNetto + '\'' +
                ", oferta=" + oferta +
                '}';
    }
}
