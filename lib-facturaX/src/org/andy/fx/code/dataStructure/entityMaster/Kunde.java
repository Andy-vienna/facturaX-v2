package org.andy.fx.code.dataStructure.entityMaster;

import jakarta.persistence.*;

@Entity
@Table(name = "tblkunde")
public class Kunde {
	@Column(name = "ansprechpartner", nullable = false)
    private String person;
	
	@Column(name = "ebillleitwegid", nullable = false)
    private String leitwegId;
	
	@Column(name = "ebillmail", nullable = false)
    private String eBillMail;
    
    @Column(name = "ebillphone", nullable = false)
    private String eBillPhone;
    
    @Column(name = "ebilltyp", nullable = false)
    private String eBillTyp;
	
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    
    @Column(name = "land", nullable = false)
    private String land;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "ort", nullable = false)
    private String ort;
    
    @Column(name = "plz", nullable = false)
    private String plz;

    @Column(name = "pronomen", nullable = false)
    private String pronomen;
    
    @Column(name = "rabattschluessel", nullable = false)
    private String deposit;
    
    @Column(name = "steuersatz", nullable = false)
    private String taxvalue;

    @Column(name = "strasse", nullable = false)
    private String strasse;

    @Column(name = "uid", nullable = false)
    private String ustid;
    
    @Column(name = "zahlungsziel", nullable = false)
    private String zahlungsziel;
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getLeitwegId() {
		return leitwegId;
	}

	public void setLeitwegId(String leitwegId) {
		this.leitwegId = leitwegId;
	}

	public String geteBillMail() {
		return eBillMail;
	}

	public void seteBillMail(String eBillMail) {
		this.eBillMail = eBillMail;
	}

	public String geteBillPhone() {
		return eBillPhone;
	}

	public void seteBillPhone(String eBillPhone) {
		this.eBillPhone = eBillPhone;
	}

	public String geteBillTyp() {
		return eBillTyp;
	}

	public void seteBillTyp(String eBillTyp) {
		this.eBillTyp = eBillTyp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getPronomen() {
		return pronomen;
	}

	public void setPronomen(String pronomen) {
		this.pronomen = pronomen;
	}

	public String getDeposit() {
		return deposit;
	}

	public void setDeposit(String deposit) {
		this.deposit = deposit;
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

	public String getZahlungsziel() {
		return zahlungsziel;
	}

	public void setZahlungsziel(String zahlungsziel) {
		this.zahlungsziel = zahlungsziel;
	}
    
}
