package com.jjbin.mysite.api.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    private String phone;   //공백없는 문자열 핸드폰번호

    @Embedded
    private Address address;



    @Builder
    public Member( String name, String phone, Address address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }
}
