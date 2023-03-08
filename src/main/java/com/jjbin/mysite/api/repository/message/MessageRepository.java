package com.jjbin.mysite.api.repository.mail;


import com.jjbin.mysite.api.domain.Mail;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MailRepository extends JpaRepository<Mail, Long>,MailRepositoryCustom {

}
