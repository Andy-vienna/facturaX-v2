package org.andy.fx.code.dataStructure.entityProductive;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblhelper")
public class Helper {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "year")
	private int year;
	
	@Column(name = "spprinted")
    private int spPrinted;
	
	@Column(name = "username", nullable = false, length = 64)
	private String userName;
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getSpPrinted() {
		return spPrinted;
	}

	public void setSpPrinted(int spPrinted) {
		this.spPrinted = spPrinted;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
