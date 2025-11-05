package org.andy.fx.code.dataStructure.entityProductive;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Nationalized;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tblan")
public class Angebot {

	@Id
	@Column(name = "IdNummer", nullable = false)
	private String idNummer;

	@Column(name = "Jahr", nullable = false)
	private int jahr;

	@Column(name = "State", nullable = false)
	private int state;

	@Column(name = "Datum", nullable = false)
	private LocalDate datum;

	@Column(name = "LZeitr", nullable = false)
	private String lZeitr;

	@Column(name = "Ref", nullable = false)
	private String ref;

	@Column(name = "IdKunde", nullable = false)
	private String idKunde;

	@Column(name = "RevCharge", nullable = false)
	private int revCharge;
	
	@Column(name = "Page2", nullable = false)
	private int page2;
	
	@Lob
	@Nationalized                  // SQL Server → NVARCHAR(MAX), Postgres ignoriert
	@Basic(fetch = FetchType.EAGER)
	@JdbcTypeCode(SqlTypes.LONGVARCHAR)  // Hibernate 6/7: klares Signal „großer Text“
	@Column(name = "seite2_html", nullable = true)
	private String beschreibungHtml;

	@Column(name = "IdBank", nullable = false)
	private int idBank;

	@Column(name = "Netto", precision = 9, scale = 2, nullable = false)
	private BigDecimal netto;

	@Column(name = "USt", precision = 9, scale = 2, nullable = false)
	private BigDecimal ust;

	@Column(name = "Brutto", precision = 9, scale = 2, nullable = false)
	private BigDecimal brutto;

	@Column(name = "AnzPos", nullable = false)
	private int anzPos;

	@Column(name = "Art01", nullable = false)
	private String art01;
	@Column(name = "Menge01", precision = 9, scale = 2, nullable = false)
	private BigDecimal menge01;
	@Column(name = "EPreis01", precision = 9, scale = 2, nullable = false)
	private BigDecimal ePreis01;

	@Column(name = "Art02")
	private String art02;
	@Column(name = "Menge02", precision = 9, scale = 2)
	private BigDecimal menge02;
	@Column(name = "EPreis02", precision = 9, scale = 2)
	private BigDecimal ePreis02;

	@Column(name = "Art03")
	private String art03;
	@Column(name = "Menge03", precision = 9, scale = 2)
	private BigDecimal menge03;
	@Column(name = "EPreis03", precision = 9, scale = 2)
	private BigDecimal ePreis03;

	@Column(name = "Art04")
	private String art04;
	@Column(name = "Menge04", precision = 9, scale = 2)
	private BigDecimal menge04;
	@Column(name = "EPreis04", precision = 9, scale = 2)
	private BigDecimal ePreis04;

	@Column(name = "Art05")
	private String art05;
	@Column(name = "Menge05", precision = 9, scale = 2)
	private BigDecimal menge05;
	@Column(name = "EPreis05", precision = 9, scale = 2)
	private BigDecimal ePreis05;

	@Column(name = "Art06")
	private String art06;
	@Column(name = "Menge06", precision = 9, scale = 2)
	private BigDecimal menge06;
	@Column(name = "EPreis06", precision = 9, scale = 2)
	private BigDecimal ePreis06;

	@Column(name = "Art07")
	private String art07;
	@Column(name = "Menge07", precision = 9, scale = 2)
	private BigDecimal menge07;
	@Column(name = "EPreis07", precision = 9, scale = 2)
	private BigDecimal ePreis07;

	@Column(name = "Art08")
	private String art08;
	@Column(name = "Menge08", precision = 9, scale = 2)
	private BigDecimal menge08;
	@Column(name = "EPreis08", precision = 9, scale = 2)
	private BigDecimal ePreis08;

	@Column(name = "Art09")
	private String art09;
	@Column(name = "Menge09", precision = 9, scale = 2)
	private BigDecimal menge09;
	@Column(name = "EPreis09", precision = 9, scale = 2)
	private BigDecimal ePreis09;

	@Column(name = "Art10")
	private String art10;
	@Column(name = "Menge10", precision = 9, scale = 2)
	private BigDecimal menge10;
	@Column(name = "EPreis10", precision = 9, scale = 2)
	private BigDecimal ePreis10;

	@Column(name = "Art11")
	private String art11;
	@Column(name = "Menge11", precision = 9, scale = 2)
	private BigDecimal menge11;
	@Column(name = "EPreis11", precision = 9, scale = 2)
	private BigDecimal ePreis11;

	@Column(name = "Art12")
	private String art12;
	@Column(name = "Menge12", precision = 9, scale = 2)
	private BigDecimal menge12;
	@Column(name = "EPreis12", precision = 9, scale = 2)
	private BigDecimal ePreis12;
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public String getIdNummer() {
		return idNummer;
	}

	public void setIdNummer(String idNummer) {
		this.idNummer = idNummer;
	}

	public int getJahr() {
		return jahr;
	}

	public void setJahr(int jahr) {
		this.jahr = jahr;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public LocalDate getDatum() {
		return datum;
	}

	public void setDatum(LocalDate datum) {
		this.datum = datum;
	}

	public String getlZeitr() {
		return lZeitr;
	}

	public void setlZeitr(String lZeitr) {
		this.lZeitr = lZeitr;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getIdKunde() {
		return idKunde;
	}

	public void setIdKunde(String idKunde) {
		this.idKunde = idKunde;
	}

	public int getRevCharge() {
		return revCharge;
	}

	public void setRevCharge(int revCharge) {
		this.revCharge = revCharge;
	}
	
	public int getPage2() {
		return page2;
	}

	public void setPage2(int page2) {
		this.page2 = page2;
	}

	public int getIdBank() {
		return idBank;
	}

	public void setIdBank(int idBank) {
		this.idBank = idBank;
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

	public int getAnzPos() {
		return anzPos;
	}

	public void setAnzPos(int anzPos) {
		this.anzPos = anzPos;
	}

	public String getArt01() {
		return art01;
	}

	public void setArt01(String art01) {
		this.art01 = art01;
	}

	public BigDecimal getMenge01() {
		return menge01;
	}

	public void setMenge01(BigDecimal menge01) {
		this.menge01 = menge01;
	}

	public BigDecimal getePreis01() {
		return ePreis01;
	}

	public void setePreis01(BigDecimal ePreis01) {
		this.ePreis01 = ePreis01;
	}

	public String getArt02() {
		return art02;
	}

	public void setArt02(String art02) {
		this.art02 = art02;
	}

	public BigDecimal getMenge02() {
		return menge02;
	}

	public void setMenge02(BigDecimal menge02) {
		this.menge02 = menge02;
	}

	public BigDecimal getePreis02() {
		return ePreis02;
	}

	public void setePreis02(BigDecimal ePreis02) {
		this.ePreis02 = ePreis02;
	}

	public String getArt03() {
		return art03;
	}

	public void setArt03(String art03) {
		this.art03 = art03;
	}

	public BigDecimal getMenge03() {
		return menge03;
	}

	public void setMenge03(BigDecimal menge03) {
		this.menge03 = menge03;
	}

	public BigDecimal getePreis03() {
		return ePreis03;
	}

	public void setePreis03(BigDecimal ePreis03) {
		this.ePreis03 = ePreis03;
	}

	public String getArt04() {
		return art04;
	}

	public void setArt04(String art04) {
		this.art04 = art04;
	}

	public BigDecimal getMenge04() {
		return menge04;
	}

	public void setMenge04(BigDecimal menge04) {
		this.menge04 = menge04;
	}

	public BigDecimal getePreis04() {
		return ePreis04;
	}

	public void setePreis04(BigDecimal ePreis04) {
		this.ePreis04 = ePreis04;
	}

	public String getArt05() {
		return art05;
	}

	public void setArt05(String art05) {
		this.art05 = art05;
	}

	public BigDecimal getMenge05() {
		return menge05;
	}

	public void setMenge05(BigDecimal menge05) {
		this.menge05 = menge05;
	}

	public BigDecimal getePreis05() {
		return ePreis05;
	}

	public void setePreis05(BigDecimal ePreis05) {
		this.ePreis05 = ePreis05;
	}

	public String getArt06() {
		return art06;
	}

	public void setArt06(String art06) {
		this.art06 = art06;
	}

	public BigDecimal getMenge06() {
		return menge06;
	}

	public void setMenge06(BigDecimal menge06) {
		this.menge06 = menge06;
	}

	public BigDecimal getePreis06() {
		return ePreis06;
	}

	public void setePreis06(BigDecimal ePreis06) {
		this.ePreis06 = ePreis06;
	}

	public String getArt07() {
		return art07;
	}

	public void setArt07(String art07) {
		this.art07 = art07;
	}

	public BigDecimal getMenge07() {
		return menge07;
	}

	public void setMenge07(BigDecimal menge07) {
		this.menge07 = menge07;
	}

	public BigDecimal getePreis07() {
		return ePreis07;
	}

	public void setePreis07(BigDecimal ePreis07) {
		this.ePreis07 = ePreis07;
	}

	public String getArt08() {
		return art08;
	}

	public void setArt08(String art08) {
		this.art08 = art08;
	}

	public BigDecimal getMenge08() {
		return menge08;
	}

	public void setMenge08(BigDecimal menge08) {
		this.menge08 = menge08;
	}

	public BigDecimal getePreis08() {
		return ePreis08;
	}

	public void setePreis08(BigDecimal ePreis08) {
		this.ePreis08 = ePreis08;
	}

	public String getArt09() {
		return art09;
	}

	public void setArt09(String art09) {
		this.art09 = art09;
	}

	public BigDecimal getMenge09() {
		return menge09;
	}

	public void setMenge09(BigDecimal menge09) {
		this.menge09 = menge09;
	}

	public BigDecimal getePreis09() {
		return ePreis09;
	}

	public void setePreis09(BigDecimal ePreis09) {
		this.ePreis09 = ePreis09;
	}

	public String getArt10() {
		return art10;
	}

	public void setArt10(String art10) {
		this.art10 = art10;
	}

	public BigDecimal getMenge10() {
		return menge10;
	}

	public void setMenge10(BigDecimal menge10) {
		this.menge10 = menge10;
	}

	public BigDecimal getePreis10() {
		return ePreis10;
	}

	public void setePreis10(BigDecimal ePreis10) {
		this.ePreis10 = ePreis10;
	}

	public String getArt11() {
		return art11;
	}

	public void setArt11(String art11) {
		this.art11 = art11;
	}

	public BigDecimal getMenge11() {
		return menge11;
	}

	public void setMenge11(BigDecimal menge11) {
		this.menge11 = menge11;
	}

	public BigDecimal getePreis11() {
		return ePreis11;
	}

	public void setePreis11(BigDecimal ePreis11) {
		this.ePreis11 = ePreis11;
	}

	public String getArt12() {
		return art12;
	}

	public void setArt12(String art12) {
		this.art12 = art12;
	}

	public BigDecimal getMenge12() {
		return menge12;
	}

	public void setMenge12(BigDecimal menge12) {
		this.menge12 = menge12;
	}

	public BigDecimal getePreis12() {
		return ePreis12;
	}

	public void setePreis12(BigDecimal ePreis12) {
		this.ePreis12 = ePreis12;
	}

	public String getBeschreibungHtml() {
		return beschreibungHtml;
	}

	public void setBeschreibungHtml(String beschreibungHtml) {
		this.beschreibungHtml = beschreibungHtml;
	}

}
