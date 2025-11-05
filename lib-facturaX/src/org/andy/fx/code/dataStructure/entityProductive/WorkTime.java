package org.andy.fx.code.dataStructure.entityProductive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblworktime")
public class WorkTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "username", nullable = false, length = 64)
	private String userName;
	
	@Column(name = "ts_local_IN", nullable = false)
	private LocalDateTime tsLocalIN;
	
	@Column(name = "ts_local_BREAK_START")
	private LocalDateTime tsLocalBS;
	
	@Column(name = "ts_local_BREAK_END")
	private LocalDateTime tsLocalBE;
	
	@Column(name = "ts_local_OUT")
	private LocalDateTime tsLocalOUT;
	
	@Column(name = "last_event", nullable = false, length = 16)
	private String lastEvent;
	
	@Column(name ="note", length = 255)
	private String note;
	
	@Column(name = "source", nullable = false, length = 32)
	private String source;
	
	@Column(name = "device_id", nullable = false, length = 64)
	private String deviceId;
	
	@Column(name="sum")
	private BigDecimal sumHours;
	
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
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public LocalDateTime getTsLocalIN() {
		return tsLocalIN;
	}

	public void setTsLocalIN(LocalDateTime tsLocalIN) {
		this.tsLocalIN = tsLocalIN;
	}

	public LocalDateTime getTsLocalBS() {
		return tsLocalBS;
	}

	public void setTsLocalBS(LocalDateTime tsLocalBS) {
		this.tsLocalBS = tsLocalBS;
	}

	public LocalDateTime getTsLocalBE() {
		return tsLocalBE;
	}

	public void setTsLocalBE(LocalDateTime tsLocalBE) {
		this.tsLocalBE = tsLocalBE;
	}

	public LocalDateTime getTsLocalOUT() {
		return tsLocalOUT;
	}

	public void setTsLocalOUT(LocalDateTime tsLocalOUT) {
		this.tsLocalOUT = tsLocalOUT;
	}
	
	public String getLastEvent() {
		return lastEvent;
	}

	public void setLastEvent(String lastEvent) {
		this.lastEvent = lastEvent;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public BigDecimal getSumHours() {
		return sumHours;
	}

	public void setSumHours(BigDecimal sumHours) {
		this.sumHours = sumHours;
	}
		
}
