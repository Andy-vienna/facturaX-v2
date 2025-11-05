package org.andy.fx.code.dataStructure.entityProductive;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tblpu")
public class Einkauf {

    @Id
    @Column(name = "Id", nullable = false)
    private String id;

    @Column(name = "re_datum", nullable = false)
    private LocalDate reDatum;

    @Column(name = "Jahr", nullable = false)
    private int jahr;

    @Column(name = "lieferant_id", nullable = false)
    private String lieferantId;

    @Column(name = "waehrung", nullable = false)
    private String waehrung;

    @Column(name = "netto", nullable = false, precision = 9, scale = 2)
    private BigDecimal netto;

    @Column(name = "ust", nullable = false, precision = 9, scale = 2)
    private BigDecimal ust;

    @Column(name = "brutto", nullable = false, precision = 9, scale = 2)
    private BigDecimal brutto;

    @Column(name = "anzahlung", nullable = false, precision = 9, scale = 2)
    private BigDecimal anzahlung;
    
    @Column(name = "Skonto1Tage", nullable = false)
	private int skonto1tage;
	
	@Column(name = "Skonto1Wert", precision = 9, scale = 3, nullable = false)
	private BigDecimal skonto1wert;
	
	@Column(name = "Skonto2Tage", nullable = false)
	private int skonto2tage;
	
	@Column(name = "Skonto2Wert", precision = 9, scale = 3, nullable = false)
	private BigDecimal skonto2wert;

    @Column(name = "zahlungsziel", nullable = false)
    private LocalDate zahlungsziel;

    @Column(name = "hinweis", nullable = false)
    private String hinweis;

    @Column(name = "dateiname", nullable = false)
    private String dateiname;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "datei", nullable = false)
    private byte[] datei;

    @Column(name = "status", nullable = false)
    private int status;
    
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getReDatum() {
        return reDatum;
    }
    public void setReDatum(LocalDate reDatum) {
        this.reDatum = reDatum;
    }

    public int getJahr() {
        return jahr;
    }
    public void setJahr(int jahr) {
        this.jahr = jahr;
    }

    public String getLieferantId() {
        return lieferantId;
    }
    public void setLieferantId(String lieferantId) {
        this.lieferantId = lieferantId;
    }

    public String getWaehrung() {
        return waehrung;
    }
    public void setWaehrung(String waehrung) {
        this.waehrung = waehrung;
    }

    public BigDecimal getNetto() {
        return netto;
    }
    public void setNetto(BigDecimal netto) {
        this.netto = netto;
    }

    public BigDecimal getUst() {
        return ust;
    }
    public void setUst(BigDecimal ust) {
        this.ust = ust;
    }

    public BigDecimal getBrutto() {
        return brutto;
    }
    public void setBrutto(BigDecimal brutto) {
        this.brutto = brutto;
    }

    public BigDecimal getAnzahlung() {
        return anzahlung;
    }
    public void setAnzahlung(BigDecimal anzahlung) {
        this.anzahlung = anzahlung;
    }

    public LocalDate getZahlungsziel() {
        return zahlungsziel;
    }
    public void setZahlungsziel(LocalDate zahlungsziel) {
        this.zahlungsziel = zahlungsziel;
    }

    public String getHinweis() {
        return hinweis;
    }
    public void setHinweis(String hinweis) {
        this.hinweis = hinweis;
    }

    public String getDateiname() {
        return dateiname;
    }
    public void setDateiname(String dateiname) {
        this.dateiname = dateiname;
    }

    public byte[] getDatei() {
        return datei;
    }
    public void setDatei(byte[] datei) {
        this.datei = datei;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
	public int getSkonto1tage() {
		return skonto1tage;
	}
	public void setSkonto1tage(int skonto1tage) {
		this.skonto1tage = skonto1tage;
	}
	public BigDecimal getSkonto1wert() {
		return skonto1wert;
	}
	public void setSkonto1wert(BigDecimal skonto1wert) {
		this.skonto1wert = skonto1wert;
	}
	public int getSkonto2tage() {
		return skonto2tage;
	}
	public void setSkonto2tage(int skonto2tage) {
		this.skonto2tage = skonto2tage;
	}
	public BigDecimal getSkonto2wert() {
		return skonto2wert;
	}
	public void setSkonto2wert(BigDecimal skonto2wert) {
		this.skonto2wert = skonto2wert;
	}
}

