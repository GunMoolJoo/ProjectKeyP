package com.example.myapplication;


/*
 사용자 계정 정보 클래스
 */
public class UserAccount {
    private String idToken; //Firebase Uid(고유토큰정보)
    private  String emailTd;    //이메일 아이디
    private String password;    //비밀번호

    public UserAccount(){ }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailTd() {
        return emailTd;
    }

    public void setEmailTd(String emailTd) {
        this.emailTd = emailTd;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
