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

    private Member from;
    private Member to;
    @BeforeEach
    void beforeEach(){
        memberRepository.deleteAll();
        messageRepository.deleteAll();
        from = memberRepository.save(
                Member.builder()
                        .name("보내는이")
                        .loginId("송신아이디")
                        .build()
        );
        to = memberRepository.save(
                Member.builder()
                        .name("받는이")
                        .loginId("수신아이디")
                        .build()
        );
    }

    @Test
    @DisplayName("메세지 작성")
    void test1() {
        //given
        MessageCreate messageCreate = MessageCreate.builder()
                .toLoginId(to.getLoginId())
                .content("내용")
                .build();

        //when
        Long messageId = messageService.Write(messageCreate, from);
        Message message = messageRepository.findById(messageId).orElse(null);
        //then
        assertThat(messageRepository.count()).isEqualTo(1L);
        assertThat(message.getContent()).isEqualTo("내용");
        assertThat(message.getTo()).isEqualTo(to);
        assertThat(message.getFrom()).isEqualTo(from);

    }

    @Test
    @DisplayName("메세지 작성 - 존재하지 않는 대상")
    void test1_2() {
        //given
        MessageCreate messageCreate = MessageCreate.builder()
                .toLoginId(to.getLoginId())
                .content("내용")
                .build();
        memberRepository.deleteById(to.getId());

        // expected
        assertThatThrownBy(() -> messageService.Write(messageCreate, from))
                .isInstanceOf(ObjectNotFound.class);
    }

    @Test
    @DisplayName("메세지 단건 조회")
    void test2() {
        //given
        MessageCreate messageCreate = MessageCreate.builder()
                .toLoginId(to.getLoginId())
                .content("내용")
                .build();
        Long messageId = messageService.Write(messageCreate, from);

        //when
        Message findOne = messageService.findOne(messageId);

        //then
        assertThat(findOne.getId()).isEqualTo(messageId);
        assertThat(findOne.getContent()).isEqualTo("내용");
        assertThat(findOne.getFrom()).isEqualTo(from);
        assertThat(findOne.getTo()).isEqualTo(to);
    }

    @Test
    @DisplayName("메세지 단건 조회 - 존재하지 않는 메세지")
    void test2_2() {
        //given
        MessageCreate messageCreate = MessageCreate.builder()
                .toLoginId(to.getLoginId())
                .content("내용")
                .build();
        Long messageId = messageService.Write(messageCreate, from);

        // expected
        assertThatThrownBy(()-> messageService.findOne(messageId+1))
                .isInstanceOf(ObjectNotFound.class);

    }

    @Test
    @DisplayName("보낸 메세지 리스트 조회")
    void test3(){
        //given
        for (int i = 0; i < 5; i++) {
            MessageCreate messageCreate = MessageCreate.builder()
                    .toLoginId(to.getLoginId())
                    .content("내용"+i)
                    .build();
            messageService.Write(messageCreate, from);
            messageCreate = MessageCreate.builder()
                    .toLoginId(from.getLoginId())
                    .content("XX"+i)
                    .build();
            messageService.Write(messageCreate, to);
        }

        // when
        List<Message> fromList = messageService.findFromList(new SearchOption(), from.getId());

        //then
        assertThat(fromList.size()).isEqualTo(5);
        for (int i = 0; i < 5; i++) {
            assertThat(fromList.get(i).getContent()).isEqualTo("내용"+i);
            assertThat(fromList.get(i).getFrom().getName()).isEqualTo("송신아이디");
        }
    }

    @Test
    @DisplayName("받은 메세지 리스트 조회")
    void test4(){
        //given
        for (int i = 0; i < 5; i++) {
            MessageCreate messageCreate = MessageCreate.builder()
                    .toLoginId(to.getLoginId())
                    .content("내용"+i)
                    .build();
            messageService.Write(messageCreate, from);
            messageCreate = MessageCreate.builder()
                    .toLoginId(from.getLoginId())
                    .content("XX"+i)
                    .build();
            messageService.Write(messageCreate, to);
        }

        // when
        List<Message> toList = messageService.findToList(new SearchOption(), from.getId());

        //then
        assertThat(toList.size()).isEqualTo(5);
        for (int i = 0; i < 5; i++) {
            assertThat(toList.get(i).getContent()).isEqualTo("내용"+i);
            assertThat(toList.get(i).getTo().getName()).isEqualTo("수신아이디");
        }
    }
}