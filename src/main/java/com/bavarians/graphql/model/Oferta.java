package com.bavarians.graphql.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity(name = "oferta")
@Table(name = "oferta")
public class Oferta {
    @Id
    @SequenceGenerator(name="seq",sequenceName="bav_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "edytowano", nullable = false)
    private Date edytowano;

    @Column(name = "dataRealizacji")
    private Date dataRealizacji;

    @Column(name = "status")
    private String status;

    @Column(name = "sumaRobociznaNetto")
    private BigDecimal sumaRobociznaNetto;

    @Column(name = "sumaCzesciBrutto")
    private BigDecimal sumaCzesciBrutto;

    @Column(name = "suma")
    private BigDecimal suma;

    @Column(name = "sumaBezVat")
    private BigDecimal sumaBezVat;

    @Column(name = "zrealizowana")
    private boolean zrealizowana;

    @Column(name = "przebieg")
    private String przebieg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pojazd_id")
    private Pojazd pojazd;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "oferta")
    private List<Element> elementySerwisowe = new LinkedList<>();

    public Oferta() {
    }

    public Oferta(Long id, Date edytowano, Date dataRealizacji, String status, BigDecimal sumaRobociznaNetto, BigDecimal sumaCzesciBrutto, BigDecimal suma, BigDecimal sumaBezVat, boolean zrealizowana, String przebieg, Pojazd pojazd, List<Element> elementySerwisowe) {
        this.id = id;
        this.edytowano = edytowano;
        this.dataRealizacji = dataRealizacji;
        this.status = status;
        this.sumaRobociznaNetto = sumaRobociznaNetto;
        this.sumaCzesciBrutto = sumaCzesciBrutto;
        this.suma = suma;
        this.sumaBezVat = sumaBezVat;
        this.zrealizowana = zrealizowana;
        this.przebieg = przebieg;
        this.pojazd = pojazd;
        this.elementySerwisowe = elementySerwisowe;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getEdytowano() {
        return edytowano;
    }

    public void setEdytowano(Date edytowano) {
        this.edytowano = edytowano;
    }

    public Date getDataRealizacji() {
        return dataRealizacji;
    }

    public void setDataRealizacji(Date dataRealizacji) {
        this.dataRealizacji = dataRealizacji;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isZrealizowana() {
        return zrealizowana;
    }

    public void setZrealizowana(boolean zrealizowana) {
        this.zrealizowana = zrealizowana;
    }

    public Pojazd getPojazd() {
        return pojazd;
    }

    public void setPojazd(Pojazd pojazd) {
        this.pojazd = pojazd;
    }

    public List<Element> getElementySerwisowe() {
        return elementySerwisowe;
    }

    public void setElementySerwisowe(List<Element> elementySerwisowe) {
        this.elementySerwisowe = elementySerwisowe;
    }

    public BigDecimal getSumaRobociznaNetto() {
        return sumaRobociznaNetto;
    }

    public void setSumaRobociznaNetto(BigDecimal sumaRobociznaNetto) {
        this.sumaRobociznaNetto = sumaRobociznaNetto;
    }

    public BigDecimal getSumaCzesciBrutto() {
        return sumaCzesciBrutto;
    }

    public void setSumaCzesciBrutto(BigDecimal sumaCzesciBrutto) {
        this.sumaCzesciBrutto = sumaCzesciBrutto;
    }

    public BigDecimal getSuma() {
        return suma;
    }

    public void setSuma(BigDecimal suma) {
        this.suma = suma;
    }

    public BigDecimal getSumaBezVat() {
        return sumaBezVat;
    }

    public void setSumaBezVat(BigDecimal sumaBezVat) {
        this.sumaBezVat = sumaBezVat;
    }

    public String getPrzebieg() {
        return przebieg;
    }

    public void setPrzebieg(String przebieg) {
        this.przebieg = przebieg;
    }

    @Override
    public String toString() {
        return "Oferta{" +
                "id=" + id +
                ", edytowano=" + edytowano +
                ", dataRealizacji=" + dataRealizacji +
                ", status='" + status + '\'' +
                ", sumaRobociznaNetto=" + sumaRobociznaNetto +
                ", sumaCzesciBrutto=" + sumaCzesciBrutto +
                ", suma=" + suma +
                ", sumaBezVat=" + sumaBezVat +
                ", zrealizowana=" + zrealizowana +
                ", pojazd=" + pojazd +
                '}';
    }
}
