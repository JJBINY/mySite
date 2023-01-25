package com.jjbin.mysite.api.service;

import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.repository.MailRepository;
import com.jjbin.mysite.api.request.MailCreate;
import com.jjbin.mysite.api.response.MailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MailService {

    private final MailRepository mailRepository;

    @Transactional
    public void save(MailCreate mailCreate) {
        Mail mail = Mail.builder()
                .title(mailCreate.getTitle())
                .content(mailCreate.getContent())
                .build();
        mailRepository.save(mail);

    }

    @Transactional
    public void delete(Long id){
        Mail mail = mailRepository.findById(id)
                .orElseThrow(ObjectNotFound::new);

        mailRepository.delete(mail);
    }

    // TODO 페이징, 검색옵션
    public List<Mail> findList(){
        return mailRepository.findAll();
    }

    public MailResponse findOne(Long id) {
        Mail mail = mailRepository.findById(id)
                .orElseThrow(ObjectNotFound::new);

        return new MailResponse(mail);
    }



}
