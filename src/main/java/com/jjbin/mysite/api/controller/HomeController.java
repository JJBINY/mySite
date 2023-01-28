package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.config.argumentresolver.Login;
import com.jjbin.mysite.api.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HomeController {

    @GetMapping("/")
    public String homeLoginArgumentResolver(@Login Member loginMember, Model model) {

        if (loginMember == null) {
            log.info("미인증 홈");
            return "home";
        }
        log.info("인증 홈");
        model.addAttribute("member", loginMember);//꺼내쓰기
        return "loginHome";
    }
}
