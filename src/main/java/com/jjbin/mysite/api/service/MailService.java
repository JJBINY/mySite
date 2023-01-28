package com.jjbin.mysite.api.service;

import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.exception.Unauthorized;
import com.jjbin.mysite.api.repository.MailRepository;
import com.jjbin.mysite.api.request.MailCreate;
import com.jjbin.mysite.api.request.MailSearch;
import com.jjbin.mysite.api.response.MailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MailService {

    private final MailRepository mailRepository;

    @Transactional
    public Long write(MailCreate mailCreate, Member member) {
        Mail mail = Mail.createMail(mailCreate, member);
        Mail save = mailRepository.save(mail);
        return save.getId();
    }

    @Transactional
    public void delete(Long id){
        Mail mail = mailRepository.findById(id)
                .orElseThrow(ObjectNotFound::new);

        mailRepository.delete(mail);
    }

    // TODO 검색옵션
    public List<Mail> findList(MailSearch mailSearch, Long memberId){
        if(mailSearch.getSize() == null){
            mailSearch.setSize(10);
        }
        if(mailSearch.getPage() == null){
            mailSearch.setPage(0);
        }
        return mailRepository
                .findAllWithMember(mailSearch, memberId);
    }

    public Mail findOne(Long id, Long memberId) {

        return mailRepository.findOneWithMember(id,memberId)
                .orElseThrow(ObjectNotFound::new);

    }



}
