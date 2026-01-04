package com.bavarians.graphql.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "pojazd")
@Table(name = "pojazd")
public class Pojazd {
    @Id
    @SequenceGenerator(name = "seq", sequenceName = "poj_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private Long id;

    @Column(name = "numerRejestracyjny")
    private String numerRejestracyjny;

    @Column(name = "marka")
    private String marka;

    @Column(name = "model")
    private String model;

    @Column(name = "vin")
    private String vin;

    @Column(name = "przebieg")
    private String przebieg;

    @Column(name = "edytowano")
    private Date edytowano = new Date();

    @Column(name = "termin_oc")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date terminOC = new Date();

    @Column(name = "termin_badania")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date terminBadania = new Date();

    @Column(name = "pow_email")
    private String powiadomieniaEmail;

    @Column(name = "aktywne_pow")
    private Boolean aktywnePowiadomienia;

    @ManyToOne
    @JoinColumn(name = "klient_id")
    private Klient wlasciciel;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pojazd")
    private List<Oferta> oferty = new ArrayList<>();

    public Pojazd() {
    }

    public Pojazd(String numerRejestracyjny, String marka, String model, String vin, String przebieg, Klient wlasciciel, List<Oferta> oferty) {
        this.numerRejestracyjny = numerRejestracyjny;
        this.marka = marka;
        this.model = model;
        this.vin = vin;
        this.przebieg = przebieg;
        this.wlasciciel = wlasciciel;
        this.oferty = oferty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumerRejestracyjny() {
        return numerRejestracyjny;
    }

    public void setNumerRejestracyjny(String numerRejestracyjny) {
        this.numerRejestracyjny = numerRejestracyjny;
    }

    public String getMarka() {
        return marka;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getPrzebieg() {
        return przebieg;
    }

    public void setPrzebieg(String przebieg) {
        this.przebieg = przebieg;
    }

    public Klient getWlasciciel() {
        return wlasciciel;
    }

    public void setWlasciciel(Klient wlasciciel) {
        this.wlasciciel = wlasciciel;
    }

    public List<Oferta> getOferty() {
        return oferty;
    }

    public void setOferty(List<Oferta> oferty) {
        this.oferty = oferty;
    }

    public Date getEdytowano() {
        return edytowano;
    }

    public void setEdytowano(Date edytowano) {
        this.edytowano = edytowano;
    }

    public Date getTerminOC() {
        return terminOC;
    }

    public void setTerminOC(Date terminOC) {
        this.terminOC = terminOC;
    }

    public Date getTerminBadania() {
        return terminBadania;
    }

    public void setTerminBadania(Date terminBadania) {
        this.terminBadania = terminBadania;
    }

    public boolean isAktywnePowiadomienia() {
        if (aktywnePowiadomienia == null) {
            return false;
        }
        return aktywnePowiadomienia;
    }

    public void setAktywnePowiadomienia(boolean aktywnePowiadomienia) {
        this.aktywnePowiadomienia = aktywnePowiadomienia;
    }

    public String getPowiadomieniaEmail() {
        return powiadomieniaEmail;
    }

    public void setPowiadomieniaEmail(String powiadomieniaEmail) {
        this.powiadomieniaEmail = powiadomieniaEmail;
    }

    @Override
    public String toString() {
        return "Pojazd{" +
                "numerRejestracyjny='" + numerRejestracyjny + '\'' +
                ", marka='" + marka + '\'' +
                ", model='" + model + '\'' +
                ", vin='" + vin + '\'' +
                ", przebieg='" + przebieg + '\'' +
                ", dataModyfikacji=" + edytowano +
                ", wlasciciel=" + wlasciciel +
                '}';
    }
}
