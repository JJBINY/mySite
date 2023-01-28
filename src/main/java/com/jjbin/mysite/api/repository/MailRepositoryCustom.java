package com.jjbin.mysite.api.repository;

import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.request.MailSearch;

import java.util.List;
import java.util.Optional;

public interface MailRepositoryCustom {

    List<Mail> findAllWithMember(MailSearch mailSearch, Long memberId);
    Optional<Mail> findOneWithMember(Long mailId, Long memberId);
}
