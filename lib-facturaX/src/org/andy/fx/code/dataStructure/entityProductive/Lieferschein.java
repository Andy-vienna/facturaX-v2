package org.andy.fx.code.dataStructure.entityProductive;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tblls")
public class Lieferschein {

	@Id
	@Column(name = "IdNummer", length = 12, nullable = false)
	private String idNummer;

	@Column(name = "Jahr", nullable = false)
	private int jahr;

	@Column(name = "State", nullable = false)
	private int state;

	@Column(name = "Datum", nullable = false)
	private LocalDate datum;

	@Column(name = "Ref", nullable = false)
	private String ref;

	@Column(name = "IdKunde", nullable = false)
	private String idKunde;
	
	@Column(name = "AnzPos", nullable = false)
	private int anzPos;

	@Column(name = "Art01", nullable = false)
	private String art01;
	@Column(name = "Menge01", precision = 9, scale = 2, nullable = false)
	private BigDecimal menge01;

	@Column(name = "Art02")
	private String art02;
	@Column(name = "Menge02", precision = 9, scale = 2)
	private BigDecimal menge02;

	@Column(name = "Art03")
	private String art03;
	@Column(name = "Menge03", precision = 9, scale = 2)
	private BigDecimal menge03;

	@Column(name = "Art04")
	private String art04;
	@Column(name = "Menge04", precision = 9, scale = 2)
	private BigDecimal menge04;
	
	@Column(name = "Art05")
	private String art05;
	@Column(name = "Menge05", precision = 9, scale = 2)
	private BigDecimal menge05;
	
	@Column(name = "Art06")
	private String art06;
	@Column(name = "Menge06", precision = 9, scale = 2)
	private BigDecimal menge06;
	
	@Column(name = "Art07")
	private String art07;
	@Column(name = "Menge07", precision = 9, scale = 2)
	private BigDecimal menge07;
	
	@Column(name = "Art08")
	private String art08;
	@Column(name = "Menge08", precision = 9, scale = 2)
	private BigDecimal menge08;
	
	@Column(name = "Art09")
	private String art09;
	@Column(name = "Menge09", precision = 9, scale = 2)
	private BigDecimal menge09;
	
	@Column(name = "Art10")
	private String art10;
	@Column(name = "Menge10", precision = 9, scale = 2)
	private BigDecimal menge10;
	
	@Column(name = "Art11")
	private String art11;
	@Column(name = "Menge11", precision = 9, scale = 2)
	private BigDecimal menge11;
	
	@Column(name = "Art12")
	private String art12;
	@Column(name = "Menge12", precision = 9, scale = 2)
	private BigDecimal menge12;
	
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
	
}
