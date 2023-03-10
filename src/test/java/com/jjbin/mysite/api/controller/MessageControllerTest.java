package com.jjbin.mysite.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbin.mysite.api.SessionConst;
import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Message;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.repository.message.MessageRepository;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.request.create.BoardCreate;
import com.jjbin.mysite.api.request.create.MessageCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MemberRepository memberRepository;

    Member from;
    Member to;
    @BeforeEach
    void beforeEach(){
        messageRepository.deleteAll();
        memberRepository.deleteAll();
        to = Member.builder().loginId("수신자아이디")
                .name("수신자")
                .build();

        from = Member.builder().loginId("송신자아이디")
                .name("송신자")
                .build();

    }

    @Test
    @DisplayName("메세지 작성 요청 - DB에 값이 저장된다")
    void test1() throws Exception {
        // given
        MessageCreate req = MessageCreate.builder()
                .toLoginId("수신자아이디")
                .content("내용")
                .build();
        MockHttpSession session = getMockHttpSession();
        String json = objectMapper.writeValueAsString(req);

        // when
        mockMvc.perform(post("/message/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Message message = messageRepository.findAll().get(0);
        assertThat(message.getTo()).isEqualTo(to);
        assertThat(message.getFrom()).isEqualTo(from);
        assertThat(message.getContent()).isEqualTo("내용");
    }



    @Test
    @DisplayName("메세지 작성 요청:실패 - 존재하지 않는 수신자아이디")
    void test1_2() throws Exception {
        // given
        MessageCreate req = MessageCreate.builder()
                .toLoginId("존재하지 않는 수신자아이디")
                .content("내용")
                .build();
        MockHttpSession session = getMockHttpSession();
        String json = objectMapper.writeValueAsString(req);

        // expected
        mockMvc.perform(post("/message/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andDo(print());

    }

    @Test
    @DisplayName("메세지 작성 요청:실패 - 인증되지 않은 요청")
    void test1_3() throws Exception {
        // given
        MessageCreate req = MessageCreate.builder()
                .toLoginId("수신자아이디")
                .content("내용")
                .build();
//        MockHttpSession session = getMockHttpSession();
        String json = objectMapper.writeValueAsString(req);

        // expected
        mockMvc.perform(post("/message/create")
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(session)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andDo(print());
    }
    @Test
    @DisplayName("메세지 작성 요청:실패 - 수신자 아이디는 필수")
    void test1_4() throws Exception {
        // given
        MessageCreate req = MessageCreate.builder()
                .content("내용")
                .build();
        MockHttpSession session = getMockHttpSession();
        String json = objectMapper.writeValueAsString(req);

        // expected
        mockMvc.perform(post("/message/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("전송할 대상을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("메세지 작성 요청:실패 - 내용은 필수")
    void test1_5() throws Exception {
        // given
        MessageCreate req = MessageCreate.builder()
                .toLoginId("수신자아이디")
//                .content("내용")
                .build();
        MockHttpSession session = getMockHttpSession();
        String json = objectMapper.writeValueAsString(req);

        // expected
        mockMvc.perform(post("/message/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("내용을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("메세지 조회 요청")
    void test2() throws Exception {
        //given
        Message save = messageRepository.save(Message.createMessage(from, to, ""));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, save.getFrom());

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/message/"+save.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toLoginId").value(to.getLoginId()))
                .andExpect(jsonPath("$.fromLoginId").value(from.getLoginId()))
                .andExpect(jsonPath("$.content").value("내용"))
                .andDo(print());
    }




    private MockHttpSession getMockHttpSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, from);
        return session;
    }
}