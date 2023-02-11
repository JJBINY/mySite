package com.jjbin.mysite.api.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String country; //나라
    private String address; //주소
    private String detail;  //상세주소

    public Address() {
    }

    public Address(String country, String address, String detail) {
        this.country = country;
        this.address = address;
        this.detail = detail;
    }
}
