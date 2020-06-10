package com.example.juniorhome.AdmissionRequest;

import java.io.Serializable;

public class ParentItemClass implements Serializable {
    private int id;
    private String usrId;
    private String name;
    private String parentStatus;
    private String address;
    private String phno;
    private String occp;
    private String qual;
    private String cnic;
    private String telno;
    private String email;
    private String dateAdded;

    public ParentItemClass(int id, String usrId, String name, String parentStatus, String address, String phno, String occp, String qual, String cnic, String telno, String email, String dateAdded) {
        this.id = id;
        this.usrId = usrId;
        this.name = name;
        this.parentStatus = parentStatus;
        this.address = address;
        this.phno = phno;
        this.occp = occp;
        this.qual = qual;
        this.cnic = cnic;
        this.telno = telno;
        this.email = email;
        this.dateAdded = dateAdded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentStatus() {
        return parentStatus;
    }

    public void setParentStatus(String parentStatus) {
        this.parentStatus = parentStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getOccp() {
        return occp;
    }

    public void setOccp(String occp) {
        this.occp = occp;
    }

    public String getQual() {
        return qual;
    }

    public void setQual(String qual) {
        this.qual = qual;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getTelno() {
        return telno;
    }

    public void setTelno(String telno) {
        this.telno = telno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}

