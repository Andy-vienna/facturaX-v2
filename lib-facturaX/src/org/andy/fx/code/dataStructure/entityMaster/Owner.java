package org.andy.fx.code.dataStructure.entityMaster;

import jakarta.persistence.*;

@Entity
@Table(name = "tblowner")
public class Owner {
	@Column(name = "adresse", nullable = false)
    private String adresse;
	
	@Column(name = "currency", nullable = false)
    private String currency;
	
	@Column(name = "kontaktname", nullable = false)
    private String kontaktName;

    @Column(name = "kontaktmail", nullable = false)
    private String kontaktMail;
    
    @Column(name = "kontakttel", nullable = false)
    private String kontaktTel;
    
    @Column(name = "land", nullable = false)
    private String land;
    
    @Id
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "ort", nullable = false)
    private String ort;
    
    @Column(name = "plz", nullable = false)
    private String plz;

    @Column(name = "taxid", nullable = false)
    private String taxid;
    
    @Column(name = "ustid", nullable = false)
    private String ustid;
    
    //###################################################################################################################################################
  	// Getter und Setter für Felder
  	//###################################################################################################################################################

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getKontaktName() {
		return kontaktName;
	}

	public void setKontaktName(String kontaktName) {
		this.kontaktName = kontaktName;
	}

	public String getKontaktMail() {
		return kontaktMail;
	}

	public void setKontaktMail(String kontaktMail) {
		this.kontaktMail = kontaktMail;
	}

	public String getKontaktTel() {
		return kontaktTel;
	}

	public void setKontaktTel(String kontaktTel) {
		this.kontaktTel = kontaktTel;
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

	public String getTaxid() {
		return taxid;
	}

	public void setTaxid(String taxid) {
		this.taxid = taxid;
	}

	public String getUstid() {
		return ustid;
	}

	public void setUstid(String ustid) {
		this.ustid = ustid;
	}
    
}
