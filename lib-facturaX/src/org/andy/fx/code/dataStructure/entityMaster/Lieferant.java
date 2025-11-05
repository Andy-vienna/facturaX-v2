package org.andy.fx.code.dataStructure.entityMaster;

import jakarta.persistence.*;

@Entity
@Table(name = "tbllieferant")
public class Lieferant {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    
    @Column(name = "kdnr", nullable = false)
    private String kdnr;
    
    @Column(name = "land", nullable = false)
    private String land;

    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "ort", nullable = false)
    private String ort;
    
    @Column(name = "plz", nullable = false)
    private String plz;
    
    @Column(name = "steuersatz", nullable = false)
    private String taxvalue;

    @Column(name = "strasse", nullable = false)
    private String strasse;
    
    @Column(name = "uid", nullable = false)
    private String ustid;
    
    //###################################################################################################################################################
  	// Getter und Setter für Felder
  	//###################################################################################################################################################

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKdnr() {
		return kdnr;
	}

	public void setKdnr(String kdnr) {
		this.kdnr = kdnr;
	}

	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		this.land = land;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getTaxvalue() {
		return taxvalue;
	}

	public void setTaxvalue(String taxvalue) {
		this.taxvalue = taxvalue;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getUstid() {
		return ustid;
	}

	public void setUstid(String ustid) {
		this.ustid = ustid;
	}
    
}
