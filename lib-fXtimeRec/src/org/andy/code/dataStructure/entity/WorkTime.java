package org.andy.code.dataStructure.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.andy.code.misc.BerlinDateTimeConverter;

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
	
	@Column(name="breaktime", nullable = false)
	private BigDecimal breakTime;
	
	@Column(name ="reason", length = 255, nullable = false)
	private String reason;
	
	@Column(name = "ts_in", nullable = false)
	@jakarta.persistence.Convert(converter = BerlinDateTimeConverter.class)
	private OffsetDateTime tsIn;
	
	@Column(name = "ts_out", nullable = false)
	@jakarta.persistence.Convert(converter = BerlinDateTimeConverter.class)
	private OffsetDateTime tsOut;
	
	@Column(name = "username", nullable = false, length = 64)
	private String username;
	
	@Column(name="worktime", nullable = false)
	private BigDecimal worktime;
	
	@Column(name="plusminus", nullable = false)
	private BigDecimal plusminus;
	
	// ###################################################################################################################################################
	// Getter und Setter
	// ###################################################################################################################################################

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getBreakTime() {
		return breakTime;
	}

	public void setBreakTime(BigDecimal breakTime) {
		this.breakTime = breakTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public OffsetDateTime getTsIn() {
		return tsIn;
	}

	public void setTsIn(OffsetDateTime tsIn) {
		this.tsIn = tsIn;
	}

	public OffsetDateTime getTsOut() {
		return tsOut;
	}

	public void setTsOut(OffsetDateTime tsOut) {
		this.tsOut = tsOut;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String userName) {
		this.username = userName;
	}

	public BigDecimal getWorkTime() {
		return worktime;
	}

	public void setWorkTime(BigDecimal workTime) {
		this.worktime = workTime;
	}
	
	public BigDecimal getPlusMinus() {
		return plusminus;
	}

	public void setPlusMinus(BigDecimal plusMinus) {
		this.plusminus = plusMinus;
	}
	
}
