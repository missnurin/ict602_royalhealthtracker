package com.example.royalcare;

public class ReadwriteUserDetails {
    public String fullName, email ,birth, gender, phone;

    public ReadwriteUserDetails(){};
    public  ReadwriteUserDetails( String textFullName, String textEmail, String textBirth, String textGender, String textPhone){
        this.fullName = textFullName;
        this.email = textEmail;
        this.birth = textBirth;
        this.gender = textGender;
        this.phone = textPhone;
    }


    public ReadwriteUserDetails(String textFullName, String textBirth, String textGender, String textPhone) {
        this.fullName = textFullName;
        this.birth = textBirth;
        this.gender = textGender;
        this.phone = textPhone;
    }
}
