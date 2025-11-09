package org.andy.fx.code.dataStructure.entityProductive;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblworktimeraw")
public class WorkTimeRaw {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "event", nullable = false, length = 12)
	private String event;

	@Column(name = "username", nullable = false, length = 64)
	private String userName;

	@Column(name = "source", nullable = false, length = 50)
	private String source;

	@Column(name = "deviceid", nullable = false, length = 64)
	private String deviceId;

	@Column(name = "tz", nullable = false, length = 64)
	private String timeZoneId;

	@Column(name = "ts", nullable = false)
	private OffsetDateTime ts;

	// ###################################################################################################################################################
	// Getter und Setter
	// ###################################################################################################################################################

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public OffsetDateTime getTs() {
		return ts;
	}

	public void setTs(OffsetDateTime ts) {
		this.ts = ts;
	}

}
