package com.jjbin.mysite.api.service;

import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.repository.MailRepository;
import com.jjbin.mysite.api.request.MailCreate;
import com.jjbin.mysite.api.response.MailResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MailServiceTest {

    @Autowired
    private MailService mailService;
    @Autowired
    private MailRepository mailRepository;

    @BeforeEach
    void clean() {
        mailRepository.deleteAll();
    }

    @Test
    @DisplayName("메일 작성")
    void createMail() {
        //given
        MailCreate mailCreate = MailCreate.builder()
                .title("제목")
                .content("내용")
                .build();

        //when
        mailService.save(mailCreate);

        //then
        assertThat(mailRepository.count()).isEqualTo(1L);
        Mail mail = mailRepository.findAll().get(0);
        assertThat(mail.getTitle()).isEqualTo("제목");
        assertThat(mail.getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("메일 1개 조회")
    void findOne() {
        // given
        Mail requestMail = Mail.builder()
                .title("제목")
                .content("내용")
                .build();
        mailRepository.save(requestMail);

        //when
        MailResponse response = mailService.findOne(requestMail.getId());

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("제목");
        assertThat(response.getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("메일 여러개 조회")
    void findList() {
        // given
        Mail requestMail = Mail.builder()
                .title("제목")
                .content("내용")
                .build();
        mailRepository.save(requestMail);

        //when
        MailResponse response = mailService.findOne(requestMail.getId());

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("제목");
        assertThat(response.getContent()).isEqualTo("내용");
    }


}