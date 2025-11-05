package org.andy.fx.code.dataStructure.entityMaster;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tbltaxvalue")
public class Tax {
	@Column(name = "arbeitspl_pausch", precision = 9, scale = 2, nullable = false)
    private BigDecimal apP;
	
	@Column(name = "bis_1", precision = 9, scale = 2, nullable = false)
    private BigDecimal bis_1;
	
	@Column(name = "bis_2", precision = 9, scale = 2, nullable = false)
    private BigDecimal bis_2;
	
	@Column(name = "bis_3", precision = 9, scale = 2, nullable = false)
    private BigDecimal bis_3;
	
	@Column(name = "bis_4", precision = 9, scale = 2, nullable = false)
    private BigDecimal bis_4;
	
	@Column(name = "bis_5", precision = 9, scale = 2, nullable = false)
    private BigDecimal bis_5;
	
	@Column(name = "bis_6", precision = 9, scale = 2, nullable = false)
    private BigDecimal bis_6;
	
	@Column(name = "bis_7", precision = 9, scale = 2, nullable = false)
    private BigDecimal bis_7;
	
    @Id
    @Column(name = "id_year")
    private int year;
    
    @Column(name = "opnv_pausch", precision = 9, scale = 2, nullable = false)
    private BigDecimal oeP;
    
    @Column(name = "tax_1", precision = 9, scale = 2, nullable = false)
    private BigDecimal tax_1;
    
    @Column(name = "tax_2", precision = 9, scale = 2, nullable = false)
    private BigDecimal tax_2;
    
    @Column(name = "tax_3", precision = 9, scale = 2, nullable = false)
    private BigDecimal tax_3;
    
    @Column(name = "tax_4", precision = 9, scale = 2, nullable = false)
    private BigDecimal tax_4;
    
    @Column(name = "tax_5", precision = 9, scale = 2, nullable = false)
    private BigDecimal tax_5;
    
    @Column(name = "tax_6", precision = 9, scale = 2, nullable = false)
    private BigDecimal tax_6;
    
    @Column(name = "tax_7", precision = 9, scale = 2, nullable = false)
    private BigDecimal tax_7;

    @Column(name = "von_1", precision = 9, scale = 2, nullable = false)
    private BigDecimal von_1;
        
    @Column(name = "von_2", precision = 9, scale = 2, nullable = false)
    private BigDecimal von_2;
        
    @Column(name = "von_3", precision = 9, scale = 2, nullable = false)
    private BigDecimal von_3;
        
    @Column(name = "von_4", precision = 9, scale = 2, nullable = false)
    private BigDecimal von_4;
        
    @Column(name = "von_5", precision = 9, scale = 2, nullable = false)
    private BigDecimal von_5;
        
    @Column(name = "von_6", precision = 9, scale = 2, nullable = false)
    private BigDecimal von_6;
        
    @Column(name = "von_7", precision = 9, scale = 2, nullable = false)
    private BigDecimal von_7;
    
    //###################################################################################################################################################
  	// Getter und Setter für Felder
  	//###################################################################################################################################################

	public BigDecimal getApP() {
		return apP;
	}

	public void setApP(BigDecimal apP) {
		this.apP = apP;
	}

	public BigDecimal getBis_1() {
		return bis_1;
	}

	public void setBis_1(BigDecimal bis_1) {
		this.bis_1 = bis_1;
	}

	public BigDecimal getBis_2() {
		return bis_2;
	}

	public void setBis_2(BigDecimal bis_2) {
		this.bis_2 = bis_2;
	}

	public BigDecimal getBis_3() {
		return bis_3;
	}

	public void setBis_3(BigDecimal bis_3) {
		this.bis_3 = bis_3;
	}

	public BigDecimal getBis_4() {
		return bis_4;
	}

	public void setBis_4(BigDecimal bis_4) {
		this.bis_4 = bis_4;
	}

	public BigDecimal getBis_5() {
		return bis_5;
	}

	public void setBis_5(BigDecimal bis_5) {
		this.bis_5 = bis_5;
	}

	public BigDecimal getBis_6() {
		return bis_6;
	}

	public void setBis_6(BigDecimal bis_6) {
		this.bis_6 = bis_6;
	}

	public BigDecimal getBis_7() {
		return bis_7;
	}

	public void setBis_7(BigDecimal bis_7) {
		this.bis_7 = bis_7;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public BigDecimal getOeP() {
		return oeP;
	}

	public void setOeP(BigDecimal oeP) {
		this.oeP = oeP;
	}

	public BigDecimal getTax_1() {
		return tax_1;
	}

	public void setTax_1(BigDecimal tax_1) {
		this.tax_1 = tax_1;
	}

	public BigDecimal getTax_2() {
		return tax_2;
	}

	public void setTax_2(BigDecimal tax_2) {
		this.tax_2 = tax_2;
	}

	public BigDecimal getTax_3() {
		return tax_3;
	}

	public void setTax_3(BigDecimal tax_3) {
		this.tax_3 = tax_3;
	}

	public BigDecimal getTax_4() {
		return tax_4;
	}

	public void setTax_4(BigDecimal tax_4) {
		this.tax_4 = tax_4;
	}

	public BigDecimal getTax_5() {
		return tax_5;
	}

	public void setTax_5(BigDecimal tax_5) {
		this.tax_5 = tax_5;
	}

	public BigDecimal getTax_6() {
		return tax_6;
	}

	public void setTax_6(BigDecimal tax_6) {
		this.tax_6 = tax_6;
	}

	public BigDecimal getTax_7() {
		return tax_7;
	}

	public void setTax_7(BigDecimal tax_7) {
		this.tax_7 = tax_7;
	}

	public BigDecimal getVon_1() {
		return von_1;
	}

	public void setVon_1(BigDecimal von_1) {
		this.von_1 = von_1;
	}

	public BigDecimal getVon_2() {
		return von_2;
	}

	public void setVon_2(BigDecimal von_2) {
		this.von_2 = von_2;
	}

	public BigDecimal getVon_3() {
		return von_3;
	}

	public void setVon_3(BigDecimal von_3) {
		this.von_3 = von_3;
	}

	public BigDecimal getVon_4() {
		return von_4;
	}

	public void setVon_4(BigDecimal von_4) {
		this.von_4 = von_4;
	}

	public BigDecimal getVon_5() {
		return von_5;
	}

	public void setVon_5(BigDecimal von_5) {
		this.von_5 = von_5;
	}

	public BigDecimal getVon_6() {
		return von_6;
	}

	public void setVon_6(BigDecimal von_6) {
		this.von_6 = von_6;
	}

	public BigDecimal getVon_7() {
		return von_7;
	}

	public void setVon_7(BigDecimal von_7) {
		this.von_7 = von_7;
	}
    
}
