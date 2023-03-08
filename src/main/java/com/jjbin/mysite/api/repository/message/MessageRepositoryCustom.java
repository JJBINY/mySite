package com.jjbin.mysite.api.repository.mail;

import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.request.SearchOption;

import java.util.List;
import java.util.Optional;

public interface MailRepositoryCustom {

    List<Mail> findAllWithMember(SearchOption searchOption, Long memberId);
    Optional<Mail> findOneWithMember(Long mailId, Long memberId);
}
