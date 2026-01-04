package com.bavarians.graphql.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;
@Entity(name = "klient")
@Table(name = "klient")
public class Klient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String email;
    @Column
    @JsonIgnore
    private String haslo;
    @JsonIgnore
    @Transient
    private String haslo2;
    @Column
    private String imie;
    @Column
    private String nazwisko;
    @ManyToMany
    private Set<Role> roles;
    @ManyToMany
    private Set<Pojazd> pojazdy;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public String getHaslo2() {
        return haslo2;
    }

    public void setHaslo2(String haslo2) {
        this.haslo2 = haslo2;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Pojazd> getPojazdy() {
        return pojazdy;
    }

    public void setPojazdy(Set<Pojazd> pojazdy) {
        this.pojazdy = pojazdy;
    }

    @Override
    public String toString() {
        return "Klient{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", haslo='" + haslo + '\'' +
                ", haslo2='" + haslo2 + '\'' +
                ", imie='" + imie + '\'' +
                ", nazwisko='" + nazwisko + '\'' +
                ", roles=" + roles +
                ", pojazdy=" + pojazdy +
                '}';
    }
}