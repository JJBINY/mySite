package com.jjbin.mysite.api.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class LoginForm {

    //빌더패턴 필요한가?
    @NotEmpty
    private String loginId;
    @NotEmpty
    private String password;
}
