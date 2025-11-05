package org.andy.fx.code.dataStructure.entityProductive;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblaz")
public class Arbeitszeit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "datei", nullable = false)
    private byte[] datei;
	
	@Column(name = "dateiname", nullable = false)
    private String dateiname;
	
	@Column(name = "jahr", nullable = false)
    private Integer jahr;
	
	@Column(name = "monat", nullable = false)
    private Integer monat;
	
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

	public byte[] getDatei() {
		return datei;
	}

	public void setDatei(byte[] datei) {
		this.datei = datei;
	}

	public String getDateiname() {
		return dateiname;
	}

	public void setDateiname(String dateiname) {
		this.dateiname = dateiname;
	}

	public Integer getJahr() {
		return jahr;
	}

	public void setJahr(Integer jahr) {
		this.jahr = jahr;
	}

	public Integer getMonat() {
		return monat;
	}

	public void setMonat(Integer monat) {
		this.monat = monat;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
		
}
