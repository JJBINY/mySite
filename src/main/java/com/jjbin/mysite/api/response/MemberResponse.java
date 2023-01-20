package com.jjbin.mysite.api.response;

import com.jjbin.mysite.api.domain.Address;
import com.jjbin.mysite.api.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MemberResponse {

    private String name;

    private String phone;   //공백없는 문자열 핸드폰번호

    private Address address;

    public MemberResponse(Member member) {
        this.name = member.getName();
        this.phone = member.getPhone();
        this.address = member.getAddress();
    }
}
