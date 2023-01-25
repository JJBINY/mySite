package com.jjbin.mysite.api.request;

import com.jjbin.mysite.api.domain.Address;
import com.jjbin.mysite.api.domain.Member;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class MemberRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    private String phone;   //공백없는 문자열 핸드폰번호

    private Address address;

    private String loginId;
    private String password;

}
