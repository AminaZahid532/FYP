package com.example.juniorhome.StaffListView;

import com.google.firebase.firestore.Exclude;

public class StaffItemClass {
    private String sname;
    private int sid;
    private String uid;
    private String dateAdded;
    private String img;
    private String experience;
    private String qualification;
    private String address;
    private int assignedNum;

    public StaffItemClass() {
        //empty constructor needed
    }

    public StaffItemClass(String sname, String img ,String Exp, String qualification, String dateAdded) {
        if (sname.trim().equals("")) {
            sname = "No Name";
        }
        this.sname = sname;
        this.img = img;
        this.experience = Exp;
        this.dateAdded = dateAdded;
        this.qualification = qualification;
    }

    public StaffItemClass(String sname, int sid, String uid, String dateAdded, String img, String experience, String qualification, String address, int assignedNum) {
        if (sname.trim().equals("")) {
            sname = "No Name";
        }
        this.sname = sname;
        this.sid = sid;
        this.uid = uid;
        this.dateAdded = dateAdded;
        this.img = img;
        this.experience = experience;
        this.qualification = qualification;
        this.address = address;
        this.assignedNum = assignedNum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getExperience() {
        return experience;
    }
    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getQualification(){return qualification;}
    public void setQualification(String qualification){this.qualification = qualification;}

    public String getSname() {
        return sname;
    }
    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getImg() {
        return img;
    }
    public void setImg(String imageUrl) {
        this.img = imageUrl;
    }

    public int getAssignedNum() { return assignedNum; }
    public void setAssignedNum(int assignedNum) { this.assignedNum = assignedNum; }
}