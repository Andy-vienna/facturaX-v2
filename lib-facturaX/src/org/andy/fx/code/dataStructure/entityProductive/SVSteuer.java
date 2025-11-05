package org.andy.fx.code.dataStructure.entityProductive;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tblsT")
public class SVSteuer {

    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Jahr", nullable = false)
    private Integer jahr;

    @Column(name = "datum", nullable = false)
    private LocalDate datum;

    @Column(name = "organisation", nullable = false)
    private String organisation;

    @Column(name = "bezeichnung", nullable = false)
    private String bezeichnung;

    @Column(name = "zahllast", precision = 9, scale = 2, nullable = false)
    private BigDecimal zahllast;

    @Column(name = "zahlungsziel", nullable = false)
    private LocalDate zahlungsziel;

    @Column(name = "dateiname", nullable = false)
    private String dateiname;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "datei", nullable = false)
    private byte[] datei;

    @Column(name = "status", nullable = false)
    private Integer status;
    
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getJahr() { return jahr; }
    public void setJahr(Integer jahr) { this.jahr = jahr; }

    public LocalDate getDatum() { return datum; }
    public void setDatum(LocalDate datum) { this.datum = datum; }

    public String getOrganisation() { return organisation; }
    public void setOrganisation(String organisation) { this.organisation = organisation; }

    public String getBezeichnung() { return bezeichnung; }
    public void setBezeichnung(String bezeichnung) { this.bezeichnung = bezeichnung; }

    public BigDecimal getZahllast() { return zahllast; }
    public void setZahllast(BigDecimal zahllast) { this.zahllast = zahllast; }

    public LocalDate getZahlungsziel() { return zahlungsziel; }
    public void setZahlungsziel(LocalDate zahlungsziel) { this.zahlungsziel = zahlungsziel; }

    public String getDateiname() { return dateiname; }
    public void setDateiname(String dateiname) { this.dateiname = dateiname; }

    public byte[] getDatei() { return datei; }
    public void setDatei(byte[] datei) { this.datei = datei; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}

