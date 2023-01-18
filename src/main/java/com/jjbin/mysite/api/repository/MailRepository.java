package com.jjbin.mysite.api.repository;


import com.jjbin.mysite.api.domain.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface MailRepository extends JpaRepository<Mail, Long> {

}
