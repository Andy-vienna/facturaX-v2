package org.andy.code.dataStructure.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblemployee")
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "username", nullable = false, length = 64)
	private String username;
	
	@Column(name = "first_name", nullable = false, length = 64)
	private String firstname;
	
	@Column(name = "last_name", length = 64)
	private String lastname;
	
	@Column(name = "address", length = 64)
	private String address;
	
	@Column(name = "zip", length = 32)
	private String zip;
	
	@Column(name = "town", length = 64)
	private String town;
	
	@Column(name = "birthday")
	private LocalDate birthday;
	
	@Column(name = "insurance_no", length = 32)
	private String insuranceno;
	
	// ###################################################################################################################################################
	// Getter und Setter
	// ###################################################################################################################################################

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String userName) {
		this.username = userName;
	}

	public String getFirstName() {
		return firstname;
	}

	public void setFirstName(String firstName) {
		this.firstname = firstName;
	}

	public String getLastName() {
		return lastname;
	}

	public void setLastName(String lastName) {
		this.lastname = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}
	
	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public String getInsuranceNo() {
		return insuranceno;
	}

	public void setInsuranceNo(String insuranceNo) {
		this.insuranceno = insuranceNo;
	}

}
