package com.jjbin.mysite.api.service;

import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.repository.MailRepository;
import com.jjbin.mysite.api.request.CreateMailRequest;
import com.jjbin.mysite.api.response.CreateMailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MailService {

    private final MailRepository mailRepository;

    @Transactional
    public Long saveMail(Mail mail) {
        Mail save = mailRepository.save(mail);
        return save.getId();
    }

    @Transactional
    public void deleteMail(Long id){
        mailRepository.deleteById(id);
    }

    public List<Mail> findMails(){
        return mailRepository.findAll();
    }

    public Optional<Mail> findOne(Long id){
        return mailRepository.findById(id);
    }



}
