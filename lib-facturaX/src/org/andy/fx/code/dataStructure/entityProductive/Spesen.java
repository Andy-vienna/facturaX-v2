package org.andy.fx.code.dataStructure.entityProductive;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tblSp")
public class Spesen {
	
	@Column(name = "amount")
    private BigDecimal amount;
	
	@Column(name = "comment")
	private String comment;
	
	@Column(name = "country")
    private String country;

    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "sumhours")
    private BigDecimal sumHours;

    @Column(name = "timestart")
    private LocalTime timeStart;
    
    @Column(name = "timeend")
    private LocalTime timeEnd;
    
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getSumHours() {
		return sumHours;
	}

	public void setSumHours(BigDecimal sumHours) {
		this.sumHours = sumHours;
	}

	public LocalTime getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(LocalTime timeStart) {
		this.timeStart = timeStart;
	}

	public LocalTime getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(LocalTime timeEnd) {
		this.timeEnd = timeEnd;
	}

}
