package com.jjbin.mysite.api.request.create;

import com.jjbin.mysite.api.domain.Address;
import com.jjbin.mysite.api.exception.Conflicted;
import com.jjbin.mysite.api.exception.InvalidRequest;
import com.jjbin.mysite.api.repository.MemberRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class MemberCreate {

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    private String phone;   //공백없는 문자열 핸드폰번호

    private Address address;

    @NotBlank(message = "로그인 아이디를 입력해주세요.")
    private String loginId;

    private String password;

    @Builder
    public MemberCreate(String name, String phone, Address address, String loginId, String password) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.loginId = loginId;
        this.password = password;

    }

    public void validate() {
        if(name.length()<2){
            throw new InvalidRequest("name", "이름은 2글자 이상이어야 합니다.");
        }
    }
}
