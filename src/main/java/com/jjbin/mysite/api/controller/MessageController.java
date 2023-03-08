package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.request.create.MailCreate;
import com.jjbin.mysite.api.request.SearchOption;
import com.jjbin.mysite.api.response.MailResponse;
import com.jjbin.mysite.api.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.jjbin.mysite.api.SessionConst.LOGIN_MEMBER;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;


    @PostMapping("/mail/create")
    public void sendMail(@RequestBody @Valid MailCreate mailCreate, HttpServletRequest request) {

        mailCreate.validate();
        Member member = (Member) request.getSession().getAttribute(LOGIN_MEMBER);

        mailService.write(mailCreate,member);
    }


    @GetMapping("/mail/list")
    public List<MailResponse> mailList(@ModelAttribute SearchOption searchOption, HttpServletRequest request) {
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        return mailService.findList(searchOption,member.getId()).stream()
                .map(MailResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/mail/{mailId}")
    public MailResponse findMail(@PathVariable Long mailId,HttpServletRequest request){
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        return new MailResponse(mailService.findOne(mailId, member.getId()));
    }

    @DeleteMapping("/mail/{mailId}")
    public void deleteMail(@PathVariable Long mailId){
        mailService.delete(mailId);
    }
}
