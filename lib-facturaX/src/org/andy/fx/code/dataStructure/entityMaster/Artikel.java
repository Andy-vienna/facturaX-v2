package org.andy.fx.code.dataStructure.entityMaster;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tblartikel")
public class Artikel {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "wert", precision = 9, scale = 2, nullable = false)
    private BigDecimal wert;

	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.toUpperCase();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BigDecimal getWert() {
        return wert;
    }

    public void setWert(BigDecimal wert) {
        this.wert = wert;
    }
}
