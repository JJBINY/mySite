package com.jjbin.mysite.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbin.mysite.api.SessionConst;
import com.jjbin.mysite.api.domain.Address;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.request.MemberCreate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        memberRepository.deleteAll();
    }
    @Test
    @DisplayName("회원가입 요청 - DB에 값이 저장된다")
    void test1() throws Exception {
        //given
        long count = memberRepository.count();
        MemberCreate request = MemberCreate.builder()
                .loginId("loginId")
                .password("1234")
                .name("jjbin")
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

        //then
        assertThat(memberRepository.count()).isEqualTo(count+1);
        Member member = memberRepository.findAll().get(0);
        assertThat(member.getLoginId()).isEqualTo("loginId");
        assertThat(member.getPassword()).isEqualTo("1234");
        assertThat(member.getName()).isEqualTo("jjbin");
    }
    @Test
    @DisplayName("회원가입 요청:실패 - loginId값은 필수")
    void test1_2() throws Exception {
        // given
        long count = memberRepository.count();
        MemberCreate request = MemberCreate.builder()
//                .loginId("loginId")
                .password("1234")
                .name("jjbin")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.loginId").value("로그인 아이디를 입력해주세요."))
                .andDo(print());
    }
    @Test
    @DisplayName("회원가입 요청:실패 - name값은 필수")
    void test1_3() throws Exception {
        // given
        long count = memberRepository.count();
        MemberCreate request = MemberCreate.builder()
                .loginId("loginId")
                .password("1234")
//                .name("jjbin")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.name").value("이름을 입력해주세요."))
                .andDo(print());
    }
    @Test
    @DisplayName("회원가입 요청:실패 - name값은 2글자 이상")
    void test1_4() throws Exception {
        // given
        long count = memberRepository.count();
        MemberCreate request = MemberCreate.builder()
                .loginId("loginId")
                .password("1234")
                .name("j")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.name").value("이름은 2글자 이상이어야 합니다."))
                .andDo(print());
    }
    @Test
    @DisplayName("회원가입 요청:실패 - 이미 존재하는 loginId")
    void test1_5() throws Exception {
        // given

        MemberCreate request = MemberCreate.builder()
                .loginId("loginId")
                .password("1234")
                .name("jjbin")
                .build();

        memberRepository.save(Member.createMember(request));

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("409"))
                .andExpect(jsonPath("$.message").value("이미 존재하는 회원 아이디"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 조회 요청")
    void test2() throws Exception {
        //given
        Member member = Member.builder()
                .loginId("loginId")
                .password("1234")
                .name("jjbin")
                .phone("010-1234-1234")
                .address(new Address("seoul", "myeonmok-ro", "123"))
                .build();
        Member saveMember = memberRepository.save(member);


        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER,saveMember);
        //expected
        mockMvc.perform(get("/member")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("loginId"))
                .andExpect(jsonPath("$.name").value("jjbin"))
                .andExpect(jsonPath("$.phone").value("010-1234-1234"))
                .andExpect(jsonPath("$.address.city").value("seoul"))
                .andExpect(jsonPath("$.address.street").value("myeonmok-ro"))
                .andExpect(jsonPath("$.address.zipcode").value("123"))
                .andDo(print());
    }
    @Test
    @DisplayName("회원 조회 요청:실패 - 인증되지 않은 요청")
    void test2_2() throws Exception {
        //expected

        mockMvc.perform(get("/member"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("인증이 필요한 요청입니다."))
                .andDo(print());
    }
//    TODO 어드민 권한이 있는 경우 전체 회원정보를 요청할 수 있다.
//    @Test
//    @DisplayName("전체 회원 조회 요청")
//    void test3() throws Exception {
//        //given
//        List<Member> reqMembers = IntStream.range(0, 10)
//                .mapToObj(i -> Member.builder()
//                        .loginId("loginId"+i)
//                        .password("1234")
//                        .name("jjbin"+i)
//                        .build())
//                .collect(Collectors.toList());
//        memberRepository.saveAll(reqMembers);
//
//        //expected
//        mockMvc.perform(get("/members"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(10))
//                .andExpect(jsonPath("$[0].loginId").value("loginId0"))
//                .andExpect(jsonPath("$[0].name").value("jjbin0"))
//                .andDo(print());
//    }
}