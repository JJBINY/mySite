package com.jjbin.mysite.api.repository;

import com.jjbin.mysite.api.domain.Address;
import com.jjbin.mysite.api.domain.Member;
import lombok.Getter;

@Getter
public class MemberDto {

    private Long id;
    private String name;

    private String phone;   //공백없는 문자열 핸드폰번호

    private Address address;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.address = member.getAddress();
    }
}
