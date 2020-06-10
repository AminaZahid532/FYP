package com.example.juniorhome;

public class UserItemClass {

    private String uname;
    private String uid;
    private String role;
    private String phoneNo;
    private String password;
    private String email;
    private String cnic;

    public String getUname() {
        return uname;
    }

    public String getUid() {
        return uid;
    }

    public String getRole() {
        return role;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getCnic() {
        return cnic;
    }

    public UserItemClass() {
        //empty constructor needed
    }

    public UserItemClass(String uname, String uid, String role, String phoneNo, String password, String email, String cnic) {
        this.uname = uname;
        this.uid = uid;
        this.role = role;
        this.phoneNo = phoneNo;
        this.password = password;
        this.email = email;
        this.cnic = cnic;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }
}