package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.request.MailCreate;
import com.jjbin.mysite.api.response.MailResponse;
import com.jjbin.mysite.api.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

//    @PostMapping("/mail")
//    public Map<String,String> mail(@RequestBody @Valid Mail mail){
//
//    }

    @PostMapping("/mail/create")
    public void createMail(@RequestBody @Valid MailCreate request) {

        request.validate();
        mailService.save(request);
    }

    @GetMapping("/mail/list")
    public List<MailResponse> mails() {
        List<Mail> mails = mailService.findList();
        return mails.stream()
                .map(MailResponse::new)
                .collect(Collectors.toList());

    }

    @GetMapping("/mail/{mailId}")
    public MailResponse findMail(@PathVariable Long mailId){
        return mailService.findOne(mailId);
    }

    @PostMapping("/mail/delete/{mailId}")
    public void deleteMail(@PathVariable Long mailId){
        mailService.delete(mailId);
    }
}
