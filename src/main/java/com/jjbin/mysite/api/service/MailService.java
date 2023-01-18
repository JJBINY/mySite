package com.jjbin.mysite.api.service;

import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.repository.MailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MailService {

    private final MailRepository mailRepository;

    @Transactional
    public Long save(Mail mail) {
        Mail save = mailRepository.save(mail);
        return save.getId();
    }

    @Transactional
    public void delete(Long id){
        mailRepository.deleteById(id);
    }

    public List<Mail> findAll(){
        return mailRepository.findAll();
    }

    public Optional<Mail> findOne(Long id){
        return mailRepository.findById(id);
    }



}
