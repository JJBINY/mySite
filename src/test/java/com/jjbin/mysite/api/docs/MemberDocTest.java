package com.jjbin.mysite.api.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbin.mysite.api.SessionConst;
import com.jjbin.mysite.api.domain.Address;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.request.create.MemberCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class MemberDocTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        memberRepository.deleteAll();
    }


    @Test
    @DisplayName("회원가입 요청")
    void test1() throws Exception {
        //given
        MemberCreate request = MemberCreate.builder()
                .loginId("로그인아이디")
                .password("비밀번호")
                .name("회원명")
                .address(new Address("국가","주소","상세주소"))
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("member-join",
                        requestFields(
                                fieldWithPath("loginId").description("로그인 ID"),
                                fieldWithPath("password").description("로그인 비밀번호"),
                                fieldWithPath("name").description("이름")
                                        .attributes(key("constraint").value("2글자 이상")),
                                fieldWithPath("phone").description("전화번호").optional(),
                                subsectionWithPath("address").description("주소").optional()

                        ),
                        requestFields(beneathPath("address"),
                                fieldWithPath("country").description("국가"),
                                fieldWithPath("address").description("주소"),
                                fieldWithPath("detail").description("상세주소")
                        )
                ));
    }

    @Test
    @DisplayName("회원가입 요청:실패")
    void test1_2() throws Exception {
        // given
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
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.loginId").value("로그인 아이디를 입력해주세요."))
                .andDo(document("member-join-fail",
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메세지"),
                                subsectionWithPath("validation").description("에러 필드")
                        )
                ));
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
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("member-find",
                        responseFields(
                                fieldWithPath("id").description("회원 ID"),
                                fieldWithPath("loginId").description("로그인 ID"),
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("phone").description("전화번호"),
                                subsectionWithPath("address").description("주소")
                        )
                ));
    }
    @Test
    @DisplayName("회원 조회 요청:실패")
    void test2_2() throws Exception {
        //expected

        mockMvc.perform(get("/member"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("인증이 필요한 요청입니다."))
                .andDo(document("member-find-fail",
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메세지"),
                                subsectionWithPath("validation").description("에러 필드")
                        )
                ));
    }
}
