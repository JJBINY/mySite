package com.jjbin.mysite.api.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String loginId;     //로그인 ID
    private String password;    //로그인 비밀번호

    private String name;        //사용자 이름

    private String phone;       //공백없는 문자열 핸드폰번호

    @Embedded
    private Address address;

    //cascade옵션 좀 더 공부 필요
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Mail> mails = new ArrayList<>();


    @Builder
    public Member( String name, String phone, Address address, String loginId, String password) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.loginId = loginId;
        this.password = password;
    }
}
