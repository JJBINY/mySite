package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.repository.MailDto;
import com.jjbin.mysite.api.request.CreateMailRequest;
import com.jjbin.mysite.api.response.CreateMailResponse;
import com.jjbin.mysite.api.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/mail")
    public Map<String,String> mail(@RequestBody @Valid Mail mail, BindingResult result){
        if (result.hasErrors()){
            FieldError fieldError = result.getFieldErrors().get(0);
            Map<String,String> error = new HashMap<>();
            error.put(fieldError.getField(), fieldError.getDefaultMessage());
            return error;
        }
        log.info("title={}",mail.getTitle());
        return Map.of();
    }

    @PostMapping("/mail/create")
    public CreateMailResponse createMail(@RequestBody @Valid CreateMailRequest request, BindingResult result){
        if (result.hasErrors()){
            FieldError fieldError = result.getFieldErrors().get(0);

            return CreateMailResponse.builder()
                            .title(fieldError.getDefaultMessage())
                                    .build();
        }
        Mail mail = Mail.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Long id = mailService.save(mail);
        return CreateMailResponse.builder()
                .id(id)
                .title(mail.getTitle())
                .build();
    }

    @GetMapping("/mail/list")
    public List<MailDto> mails() {
        List<Mail> mails = mailService.findAll();
        return mails.stream()
                .map(MailDto::new)
                .collect(Collectors.toList());

    }

    @GetMapping("/mail/{mailId}")
    public MailDto findMail(@PathVariable Long mailId){
        Mail mail = mailService.findOne(mailId).orElseThrow(IllegalArgumentException::new);
        return new MailDto(mail);
    }

    @PostMapping("/mail/delete/{mailId}")
    public void deleteMail(@PathVariable Long mailId){
        mailService.delete(mailId);
    }
}
