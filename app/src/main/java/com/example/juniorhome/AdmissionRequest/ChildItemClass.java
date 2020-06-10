package com.example.juniorhome.AdmissionRequest;

import java.io.Serializable;

public class ChildItemClass implements Serializable {
    private int id;
    private int assignedTo;
    private String parentUid;
    private String name;
    private String gender;
    private String address;
    private String bday;
    private String fname;
    private String fphno;
    private String foccp;
    private String fqual;
    private String fcnic;
    private String mname;
    private String mphno;
    private String moccp;
    private String mqual;
    private String mcnic;
    private String info;
    private String img;
    private String pay;
    private String prog;
    private String telno;
    private String email;
    private String dname;
    private String dphno;
    private String gname;
    private String gphno;
    private String dateAdded;

    public ChildItemClass() {
        //empty constructor needed
    }

    public ChildItemClass(int id,String name, String imgPath, String dateSubmit)
    {
        this.id = id;
        this.name = name;
        this.img = imgPath;
        this.dateAdded = dateSubmit;
    }

    public ChildItemClass(int id, String pid, String name, String gender, int staffId, String address, String bday, String fname, String fphno, String foccp, String fqual, String fcnic, String mname, String mphno, String moccp, String mqual, String mcnic, String info, String img, String pay, String prog, String telno, String email, String dname, String dphno, String gname, String gphno,String dateSubmit) {
        this.id = id;
        this.parentUid = pid;
        this.name = name;
        this.gender = gender;
        this.address = address;
        this.bday = bday;
        this.fname = fname;
        this.fphno = fphno;
        this.foccp = foccp;
        this.fqual = fqual;
        this.fcnic = fcnic;
        this.mname = mname;
        this.mphno = mphno;
        this.moccp = moccp;
        this.mqual = mqual;
        this.mcnic = mcnic;
        this.info = info;
        this.img = img;
        this.pay = pay;
        this.prog = prog;
        this.telno = telno;
        this.email = email;
        this.dname = dname;
        this.dphno = dphno;
        this.gname = gname;
        this.gphno = gphno;
        this.assignedTo = staffId;
        this.dateAdded = dateSubmit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDateAdded(){
        return dateAdded;
    }

    public int getAssignedTo() { return assignedTo; }

    public void setAssignedTo(int assignedTo) { this.assignedTo = assignedTo; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBday() {
        return bday;
    }

    public void setBday(String bday) {
        this.bday = bday;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFphno() {
        return fphno;
    }

    public void setFphno(String fphno) {
        this.fphno = fphno;
    }

    public String getFoccp() {
        return foccp;
    }

    public void setFoccp(String foccp) {
        this.foccp = foccp;
    }

    public String getFqual() {
        return fqual;
    }

    public void setFqual(String fqual) {
        this.fqual = fqual;
    }

    public String getFcnic() {
        return fcnic;
    }

    public void setFcnic(String fcnic) {
        this.fcnic = fcnic;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getMphno() {
        return mphno;
    }

    public void setMphno(String mphno) {
        this.mphno = mphno;
    }

    public String getMoccp() {
        return moccp;
    }

    public void setMoccp(String moccp) {
        this.moccp = moccp;
    }

    public String getMqual() {
        return mqual;
    }

    public void setMqual(String mqual) {
        this.mqual = mqual;
    }

    public String getMcnic() {
        return mcnic;
    }

    public void setMcnic(String mcnic) {
        this.mcnic = mcnic;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPay() { return pay; }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getProg() {
        return prog;
    }

    public void setProg(String prog) {
        this.prog = prog;
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

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDphno() {
        return dphno;
    }

    public void setDphno(String dphno) {
        this.dphno = dphno;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public String getGphno() {
        return gphno;
    }

    public void setGphno(String gphno) {
        this.gphno = gphno;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }
}
