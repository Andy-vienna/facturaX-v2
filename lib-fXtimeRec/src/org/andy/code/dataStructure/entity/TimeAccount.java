package org.andy.code.dataStructure.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbltimeaccount")
public class TimeAccount {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "tiprinted")
    private int tiPrinted;
	
	@Column(name = "username", nullable = false, length = 64)
	private String username;
	
	@Column(name = "contracthours")
	private BigDecimal contractHours;
	
	@Column(name = "overtime")
	private BigDecimal overTime;
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public int getTiPrinted() {
		return tiPrinted;
	}

	public void setTiPrinted(int tiPrinted) {
		this.tiPrinted = tiPrinted;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String userName) {
		this.username = userName;
	}

	public BigDecimal getContractHours() {
		return contractHours;
	}

	public void setContractHours(BigDecimal contractHours) {
		this.contractHours = contractHours;
	}

	public BigDecimal getOverTime() {
		return overTime;
	}

	public void setOverTime(BigDecimal overTime) {
		this.overTime = overTime;
	}
	
}
