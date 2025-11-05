package org.andy.fx.code.dataStructure.entityProductive;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;

@Entity
@Table(name = "tblfiles")
public class FileStore {
    @Column(name = "Jahr", nullable = false)
    private int jahr;
    
    @Column(name = "ABFileName")
    private String abFileName;

    @Column(name = "ANFileName")
    private String anFileName;

    @Column(name = "AddFileName01")
    private String addFileName01;
    
    @Column(name = "AddFileName02")
    private String addFileName02;
    
    @Column(name = "AddFileName03")
    private String addFileName03;
    
    @Column(name = "BEFileName")
    private String beFileName;
    
    @Id
    @Column(name = "IdNummer", nullable = false)
    private String idNummer;
    
    @Column(name = "LSFileName")
    private String lsFileName;
    
    @Column(name = "M1FileName")
    private String m1FileName;
    
    @Column(name = "M2FileName")
    private String m2FileName;
    
    @Column(name = "REFileName")
    private String reFileName;
    
    @Column(name = "ZEFileName")
    private String ZeFileName;
    
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "ABpdfFile")
    private byte[] abPdfFile;
    
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "ANpdfFile")
    private byte[] anPdfFile;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "AddFile01")
    private byte[] addFile01;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "AddFile02")
    private byte[] addFile02;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "AddFile03")
    private byte[] addFile03;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "BEpdfFile")
    private byte[] bePdfFile;
    
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "LSpdfFile")
    private byte[] lsPdfFile;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "M1pdfFile")
    private byte[] m1PdfFile;
    
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "M2pdfFile")
    private byte[] m2PdfFile;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "REpdfFile")
    private byte[] rePdfFile;
    
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "ZEpdfFile")
    private byte[] zePdfFile;
    
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public int getJahr() {
		return jahr;
	}

	public void setJahr(int jahr) {
		this.jahr = jahr;
	}

	public String getAbFileName() {
		return abFileName;
	}

	public void setAbFileName(String abFileName) {
		this.abFileName = abFileName;
	}

	public String getAnFileName() {
		return anFileName;
	}

	public void setAnFileName(String anFileName) {
		this.anFileName = anFileName;
	}

	public String getAddFileName01() {
		return addFileName01;
	}

	public void setAddFileName01(String addFileName01) {
		this.addFileName01 = addFileName01;
	}

	public String getAddFileName02() {
		return addFileName02;
	}

	public void setAddFileName02(String addFileName02) {
		this.addFileName02 = addFileName02;
	}

	public String getAddFileName03() {
		return addFileName03;
	}

	public void setAddFileName03(String addFileName03) {
		this.addFileName03 = addFileName03;
	}

	public String getBeFileName() {
		return beFileName;
	}

	public void setBeFileName(String beFileName) {
		this.beFileName = beFileName;
	}

	public String getIdNummer() {
		return idNummer;
	}

	public void setIdNummer(String idNummer) {
		this.idNummer = idNummer;
	}

	public String getLsFileName() {
		return lsFileName;
	}

	public void setLsFileName(String lsFileName) {
		this.lsFileName = lsFileName;
	}

	public String getM1FileName() {
		return m1FileName;
	}

	public void setM1FileName(String m1FileName) {
		this.m1FileName = m1FileName;
	}

	public String getM2FileName() {
		return m2FileName;
	}

	public void setM2FileName(String m2FileName) {
		this.m2FileName = m2FileName;
	}

	public String getReFileName() {
		return reFileName;
	}

	public void setReFileName(String reFileName) {
		this.reFileName = reFileName;
	}

	public String getZeFileName() {
		return ZeFileName;
	}

	public void setZeFileName(String zeFileName) {
		ZeFileName = zeFileName;
	}

	public byte[] getAbPdfFile() {
		return abPdfFile;
	}

	public void setAbPdfFile(byte[] abPdfFile) {
		this.abPdfFile = abPdfFile;
	}

	public byte[] getAnPdfFile() {
		return anPdfFile;
	}

	public void setAnPdfFile(byte[] anPdfFile) {
		this.anPdfFile = anPdfFile;
	}

	public byte[] getAddFile01() {
		return addFile01;
	}

	public void setAddFile01(byte[] addFile01) {
		this.addFile01 = addFile01;
	}

	public byte[] getAddFile02() {
		return addFile02;
	}

	public void setAddFile02(byte[] addFile02) {
		this.addFile02 = addFile02;
	}

	public byte[] getAddFile03() {
		return addFile03;
	}

	public void setAddFile03(byte[] addFile03) {
		this.addFile03 = addFile03;
	}

	public byte[] getBePdfFile() {
		return bePdfFile;
	}

	public void setBePdfFile(byte[] bePdfFile) {
		this.bePdfFile = bePdfFile;
	}

	public byte[] getLsPdfFile() {
		return lsPdfFile;
	}

	public void setLsPdfFile(byte[] lsPdfFile) {
		this.lsPdfFile = lsPdfFile;
	}

	public byte[] getM1PdfFile() {
		return m1PdfFile;
	}

	public void setM1PdfFile(byte[] m1PdfFile) {
		this.m1PdfFile = m1PdfFile;
	}

	public byte[] getM2PdfFile() {
		return m2PdfFile;
	}

	public void setM2PdfFile(byte[] m2PdfFile) {
		this.m2PdfFile = m2PdfFile;
	}

	public byte[] getRePdfFile() {
		return rePdfFile;
	}

	public void setRePdfFile(byte[] rePdfFile) {
		this.rePdfFile = rePdfFile;
	}

	public byte[] getZePdfFile() {
		return zePdfFile;
	}

	public void setZePdfFile(byte[] zePdfFile) {
		this.zePdfFile = zePdfFile;
	}

}
