package com.jjbin.mysite.api.service;

import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.repository.mail.MailRepository;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.request.create.MailCreate;
import com.jjbin.mysite.api.request.SearchOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MailServiceTest {

    @Autowired
    private MailService mailService;
    @Autowired
    private MailRepository mailRepository;
    @Autowired
    private MemberRepository memberRepository;


    @BeforeEach
    void beforeEach() {
        mailRepository.deleteAll();
        memberRepository.deleteAll();

        Member member = Member.builder()
                .name("이름")
                .loginId("아이디")
                .build();
        Member savedMember = memberRepository.save(member);

        for (int i = 0; i <10; i++) {


            MailCreate mailCreate = MailCreate.builder()
                    .destination("수신주소"+i)
                    .title("제목"+i)
                    .content("내용"+i)
                    .build();

            mailRepository.save(Mail.createMail(mailCreate, savedMember));
        }
        Member member2 = Member.builder()
                .name("이름2")
                .loginId("아이디2")
                .build();
        Member savedMember2 = memberRepository.save(member2);

        for (int i = 0; i <10; i++) {


            MailCreate mailCreate = MailCreate.builder()
                    .destination("수신주소2-"+i)
                    .title("제목2-"+i)
                    .content("내용2-"+i)
                    .build();

            mailRepository.save(Mail.createMail(mailCreate, savedMember2));
        }
    }

    @Test
    @DisplayName("메일 작성")
    void test1() {
        //given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        MailCreate mailCreate = MailCreate.builder()
                .destination("수신주소")
                .title("제목")
                .content("내용")
                .build();

        long count = mailRepository.count();

        //when
        Long mailId = mailService.write(mailCreate, member);
        Mail mail = mailRepository.findOneWithMember(mailId,member.getId())
                .orElseThrow(ObjectNotFound::new);

        //then
        assertThat(mailRepository.count()).isEqualTo(count+1L);
        assertThat(mail.getDestination()).isEqualTo("수신주소");
        assertThat(mail.getTitle()).isEqualTo("제목");
        assertThat(mail.getContent()).isEqualTo("내용");
        assertThat(mail.getMember().getLoginId()).isEqualTo("아이디");
        assertThat(mail.getMember().getName()).isEqualTo("이름");
    }


    @Test
    @DisplayName("메일 1개 조회")
    void test2() {
        // given

        Member member = memberRepository.save(
                Member.builder()
                .name("이름")
                .loginId("아이디")
                .build()
        );


        MailCreate mailCreate = MailCreate.builder()
                .destination("수신주소")
                .title("제목")
                .content("내용")
                .build();

        Mail mail = mailRepository.save(Mail.createMail(mailCreate, member));
        Long findId = mail.getId();

        //when
        Mail findMail = mailService.findOne(findId, member.getId());

        //then
        assertThat(findMail).isNotNull();
        assertThat(findMail.getTitle()).isEqualTo("제목");
        assertThat(findMail.getContent()).isEqualTo("내용");
    }
    @Test
    @DisplayName("메일 1개 조회 - 존재하지 않는 메일")
    void test2_1() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        MailCreate mailCreate = MailCreate.builder()
                .destination("수신주소")
                .title("제목")
                .content("내용")
                .build();

        Mail mail = mailRepository.save(Mail.createMail(mailCreate, member));

        //expected
        assertThatThrownBy(() -> mailService.findOne(mail.getId()+1,member.getId()))
                .isInstanceOf(ObjectNotFound.class);
    }


    @Test
    @DisplayName("메일 여러개 조회 - 5개씩 페이징")
    void test3() {
        //given

        SearchOption search = new SearchOption();
        search.setSize(5);
        search.setPage(2);

        Member member = memberRepository.findAll().get(0);
        //when
        List<Mail> mails = mailService.findList(search, member.getId());

        //then

        assertThat(mails).isNotNull();
        assertThat(mails.size()).isEqualTo(5);
        for (int i = 4; i >=0; i--) {
            assertThat(mails.get(4-i).getTitle()).isEqualTo("제목" + i);
        }
    }
    @Test
    @DisplayName("메일 삭제")
    void test4() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        MailCreate mailCreate = MailCreate.builder()
                .destination("수신주소")
                .title("제목")
                .content("내용")
                .build();

        Mail mail = mailRepository.save(Mail.createMail(mailCreate, member));
        long count = mailRepository.count();

        //when
        mailService.delete(mail.getId());

        //then
        assertThat(mailRepository.count()).isEqualTo(count - 1);
        assertThatThrownBy(() -> mailService.delete(mail.getId()))
                .isInstanceOf(ObjectNotFound.class);
        assertThatThrownBy(() -> mailService.findOne(mail.getId(), member.getId()))
                .isInstanceOf(ObjectNotFound.class);


    }

    @Test
    @DisplayName("메일 삭제 - 존재하지 않는 메일")
    void test4_1() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        MailCreate mailCreate = MailCreate.builder()
                .destination("수신주소")
                .title("제목")
                .content("내용")
                .build();

        Mail mail = mailRepository.save(Mail.createMail(mailCreate, member));

        //expected
        assertThatThrownBy(() -> mailService.delete(mail.getId()+1)).isInstanceOf(ObjectNotFound.class);
    }


}