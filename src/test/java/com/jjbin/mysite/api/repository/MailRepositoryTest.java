package com.jjbin.mysite.api.repository;

import com.jjbin.mysite.api.domain.Mail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j

class MailRepositoryTest {

    @Autowired
    private MailRepository mailRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void beforeEach(){
        for (int i = 0; i < 10; i++) {

            mailRepository.save(
                    Mail.builder()
                            .title("제목"+i)
                            .content("내용"+i)
                            .build()
            );
        }

    }


    @Test
    @DisplayName("메일 저장")
    void save(){
        //given
        long count = mailRepository.count();
        Mail mail = Mail.builder()
                .title("제목")
                .content("내용")
                .build();


        //when
        mailRepository.save(mail);

        Mail findMail = mailRepository.findById(mail.getId()).get();


        //then
        assertThat(mailRepository.count()).isEqualTo(count+1);
        assertThat(findMail).isEqualTo(mail);
    }


    @Test
    @DisplayName("Id로 메일 조회")
    void findById(){
        //given
        Mail mail = mailRepository.save(
                Mail.builder()
                        .title("제목")
                        .content("내용")
                        .build()
        );

        //when
        Mail findMail = mailRepository.findById(mail.getId()).get();

        //then
        assertThat(findMail).isEqualTo(mail);
    }

}