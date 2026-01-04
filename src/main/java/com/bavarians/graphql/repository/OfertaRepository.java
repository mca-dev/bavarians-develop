package com.bavarians.graphql.repository;

import com.bavarians.dto.OfertySumaDto;
import com.bavarians.graphql.model.Klient;
import com.bavarians.graphql.model.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OfertaRepository extends JpaRepository<Oferta, Long> {

    @Query("from oferta o left join pojazd p on o.pojazd=p.id where p.wlasciciel =?1")
    List<Oferta> findAllByWlasciciel(Klient id);

    @Query("from oferta o left join pojazd p on o.pojazd=p.id where o.id =?1")
    Oferta findOneWithPojazd(Long id);

    @Query(value = "select o.id from oferta o left join pojazd p on o.pojazd_id=p.id left join element_serwisowy e on e.oferta_id = o.id where o.id =?1 GROUP BY o.id", nativeQuery = true)
    Oferta findOneWithPojazdAndElements(Long id);

    @Query("from oferta o left join pojazd p on o.pojazd = p.id left join element e on e.oferta = o.id where o.id =?1 GROUP BY o.id")
    Oferta findOneWithPojazdAndElements2(Long id);

    @Query("from oferta o left join pojazd p on o.pojazd = p.id left join element e on e.oferta = o.id GROUP BY o.id, p.id, e.id ORDER BY o.edytowano DESC")
        // @Query(value = "select * from oferta o left join pojazd p on o.pojazd_id=p.id left join element_serwisowy e on e.oferta_id = o.id GROUP BY o.id, p.id, e.id", nativeQuery = true)
    List<Oferta> findAllWithPojazdAndElements();

    // @Query("select to_char(edytowano, 'YYYY-MM'), SUM(suma) from oferta GROUP BY to_char(edytowano, 'YYYY-MM') order by to_char(edytowano, 'YYYY-MM')")
    @Query("select EXTRACT(MONTH FROM o.edytowano) as miesiac, EXTRACT(YEAR FROM o.edytowano) as rok, SUM(o.suma) as suma, " +
            " SUM(o.sumaBezVat) as sumabezvat," +
            " SUM(o.sumaCzesciBrutto) as sumaczescibrutto," +
            " SUM(o.sumaRobociznaNetto) as sumarobociznanetto " +
            "from oferta o GROUP BY EXTRACT(MONTH FROM o.edytowano), EXTRACT(YEAR FROM o.edytowano) order by EXTRACT(YEAR FROM o.edytowano), EXTRACT(MONTH FROM o.edytowano)")
    List<OfertySumaDto> sumaMiesiecznyPrzychod();


}