package org.andy.fx.code.dataStructure.entityMaster;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tblgwbvalue")
public class Gwb {
	@Column(name = "bis_1", precision = 9, scale = 2, nullable = false)
    private BigDecimal bis_1;
	
    @Id
    @Column(name = "id_year")
    private int year;

    @Column(name = "val_1", precision = 9, scale = 2, nullable = false)
    private BigDecimal val_1;
    
    @Column(name = "val_2", precision = 9, scale = 2, nullable = false)
    private BigDecimal val_2;
    
    @Column(name = "val_3", precision = 9, scale = 2, nullable = false)
    private BigDecimal val_3;
    
    @Column(name = "val_4", precision = 9, scale = 2, nullable = false)
    private BigDecimal val_4;

    @Column(name = "weitere_2", precision = 9, scale = 2, nullable = false)
    private BigDecimal weitere_2;
    
    @Column(name = "weitere_3", precision = 9, scale = 2, nullable = false)
    private BigDecimal weitere_3;

    @Column(name = "weitere_4", precision = 9, scale = 2, nullable = false)
    private BigDecimal weitere_4;
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public BigDecimal getBis_1() {
		return bis_1;
	}

	public void setBis_1(BigDecimal bis_1) {
		this.bis_1 = bis_1;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public BigDecimal getVal_1() {
		return val_1;
	}

	public void setVal_1(BigDecimal val_1) {
		this.val_1 = val_1;
	}

	public BigDecimal getVal_2() {
		return val_2;
	}

	public void setVal_2(BigDecimal val_2) {
		this.val_2 = val_2;
	}

	public BigDecimal getVal_3() {
		return val_3;
	}

	public void setVal_3(BigDecimal val_3) {
		this.val_3 = val_3;
	}

	public BigDecimal getVal_4() {
		return val_4;
	}

	public void setVal_4(BigDecimal val_4) {
		this.val_4 = val_4;
	}

	public BigDecimal getWeitere_2() {
		return weitere_2;
	}

	public void setWeitere_2(BigDecimal weitere_2) {
		this.weitere_2 = weitere_2;
	}

	public BigDecimal getWeitere_3() {
		return weitere_3;
	}

	public void setWeitere_3(BigDecimal weitere_3) {
		this.weitere_3 = weitere_3;
	}

	public BigDecimal getWeitere_4() {
		return weitere_4;
	}

	public void setWeitere_4(BigDecimal weitere_4) {
		this.weitere_4 = weitere_4;
	}
    
}
