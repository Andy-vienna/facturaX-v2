package org.andy.datamigration.code.dataStructure.entityMaster;

import jakarta.persistence.*;

@Entity
@Table(name = "tblbank")
public class BankMig {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "bankname", nullable = false)
    private String bankName;
    
    @Column(name = "bic", nullable = false)
    private String bic;
    
    @Column(name = "iban", nullable = false)
    private String iban;
    
    @Column(name = "kontoinhaber", nullable = false)
    private String ktoName;

	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

    public int getId() {
        return id;
    }
    
    //public void setId(int id) {
    //	this.id = id;
    //}

    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public String getBic() {
        return bic;
    }
    
    public void setBic(String bic) {
        this.bic = bic != null ? bic.toUpperCase() : null;
    }
    
    public String getIban() {
        return iban;
    }
    
    public void setIban(String iban) {
        this.iban = iban != null ? iban.toUpperCase() : null;
    }
    
    public String getKtoName() {
        return ktoName;
    }
    
    public void setKtoName(String ktoName) {
        this.ktoName = ktoName;
    }
}
