package com.dadhwal.LedController.LedSDK;

public class CompanyInfo {
    private String company;
    private String phone;
    private String email;

    public CompanyInfo(String company,String phone,String email){
        this.company=company;
        this.phone=phone;
        this.email=email;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email){
        this.email=email;
    }

    public String getEmail() {
        return email;
    }
}
