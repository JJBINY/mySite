package com.jjbin.mysite.api.service;

import com.jjbin.mysite.api.domain.Message;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.repository.message.MessageRepository;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.request.create.MessageCreate;
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
class MessageServiceTest {

    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MemberRepository memberRepository;


    @BeforeEach
    void beforeEach() {
        messageRepository.deleteAll();
        memberRepository.deleteAll();

        Member member = Member.builder()
                .name("이름")
                .loginId("아이디")
                .build();
        Member savedMember = memberRepository.save(member);

        for (int i = 0; i <10; i++) {


            MessageCreate messageCreate = MessageCreate.builder()
                    .destination("수신주소"+i)
                    .title("제목"+i)
                    .content("내용"+i)
                    .build();

            messageRepository.save(Message.createMessage(messageCreate, savedMember));
        }
        Member member2 = Member.builder()
                .name("이름2")
                .loginId("아이디2")
                .build();
        Member savedMember2 = memberRepository.save(member2);

        for (int i = 0; i <10; i++) {


            MessageCreate messageCreate = MessageCreate.builder()
                    .destination("수신주소2-"+i)
                    .title("제목2-"+i)
                    .content("내용2-"+i)
                    .build();

            messageRepository.save(Message.createMessage(messageCreate, savedMember2));
        }
    }

    @Test
    @DisplayName("메세지 작성")
    void test1() {
        //given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        MessageCreate messageCreate = MessageCreate.builder()
                .destination("수신주소")
                .title("제목")
                .content("내용")
                .build();

        long count = messageRepository.count();

        //when
        Long mailId = messageService.Write(messageCreate, member);
        Message message = messageRepository.findOneWithMember(mailId,member.getId())
                .orElseThrow(ObjectNotFound::new);

        //then
        assertThat(messageRepository.count()).isEqualTo(count+1L);
        assertThat(message.getDestination()).isEqualTo("수신주소");
        assertThat(message.getTitle()).isEqualTo("제목");
        assertThat(message.getContent()).isEqualTo("내용");
        assertThat(message.getMember().getLoginId()).isEqualTo("아이디");
        assertThat(message.getMember().getName()).isEqualTo("이름");
    }


    @Test
    @DisplayName("메세지 1개 조회")
    void test2() {
        // given

        Member member = memberRepository.save(
                Member.builder()
                .name("이름")
                .loginId("아이디")
                .build()
        );


        MessageCreate messageCreate = MessageCreate.builder()
                .destination("수신주소")
                .title("제목")
                .content("내용")
                .build();

        Message message = messageRepository.save(Message.createMessage(messageCreate, member));
        Long findId = message.getId();

        //when
        Message findMessage = messageService.findOne(findId, member.getId());

        //then
        assertThat(findMessage).isNotNull();
        assertThat(findMessage.getTitle()).isEqualTo("제목");
        assertThat(findMessage.getContent()).isEqualTo("내용");
    }
    @Test
    @DisplayName("메세지 1개 조회 - 존재하지 않는 메세지")
    void test2_1() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        MessageCreate messageCreate = MessageCreate.builder()
                .destination("수신주소")
                .title("제목")
                .content("내용")
                .build();

        Message message = messageRepository.save(Message.createMessage(messageCreate, member));

        //expected
        assertThatThrownBy(() -> messageService.findOne(message.getId()+1,member.getId()))
                .isInstanceOf(ObjectNotFound.class);
    }


    @Test
    @DisplayName("메세지 여러개 조회 - 5개씩 페이징")
    void test3() {
        //given

        SearchOption search = new SearchOption();
        search.setSize(5);
        search.setPage(2);

        Member member = memberRepository.findAll().get(0);
        //when
        List<Message> messages = messageService.findFromList(search, member.getId());

        //then

        assertThat(messages).isNotNull();
        assertThat(messages.size()).isEqualTo(5);
        for (int i = 4; i >=0; i--) {
            assertThat(messages.get(4-i).getTitle()).isEqualTo("제목" + i);
        }
    }
    @Test
    @DisplayName("메세지 삭제")
    void test4() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        MessageCreate messageCreate = MessageCreate.builder()
                .destination("수신주소")
                .title("제목")
                .content("내용")
                .build();

        Message message = messageRepository.save(Message.createMessage(messageCreate, member));
        long count = messageRepository.count();

        //when
        messageService.delete(message.getId());

        //then
        assertThat(messageRepository.count()).isEqualTo(count - 1);
        assertThatThrownBy(() -> messageService.delete(message.getId()))
                .isInstanceOf(ObjectNotFound.class);
        assertThatThrownBy(() -> messageService.findOne(message.getId(), member.getId()))
                .isInstanceOf(ObjectNotFound.class);


    }

    @Test
    @DisplayName("메세지 삭제 - 존재하지 않는 메세지")
    void test4_1() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        MessageCreate messageCreate = MessageCreate.builder()
                .destination("수신주소")
                .title("제목")
                .content("내용")
                .build();

        Message message = messageRepository.save(Message.createMessage(messageCreate, member));

        //expected
        assertThatThrownBy(() -> messageService.delete(message.getId()+1)).isInstanceOf(ObjectNotFound.class);
    }


}