package org.andy.fx.code.dataStructure.entityMaster;

import jakarta.persistence.*;

@Entity
@Table(name = "tbluser")
public class User {
	@Column(name = "email")
	private String email;
	
	@Column(name = "hash")
    private String hash;
	
	@Id
    @Column(name = "id")
    private String id;
	
    @Column(name = "roles")
    private String roles;
    
    @Column(name = "tabconfig")
    private int tabConfig;

    //###################################################################################################################################################
  	// Getter und Setter für Felder
  	//###################################################################################################################################################

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public int getTabConfig() {
		return tabConfig;
	}

	public void setTabConfig(int tabConfig) {
		this.tabConfig = tabConfig;
	}
    
}
