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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @DisplayName("???????????? ??????")
    void test1() throws Exception {
        //given
        MemberCreate request = MemberCreate.builder()
                .loginId("??????????????????")
                .password("????????????")
                .name("?????????")
                .address(new Address("??????","??????","????????????"))
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
                                fieldWithPath("loginId").description("????????? ID"),
                                fieldWithPath("password").description("????????? ????????????"),
                                fieldWithPath("name").description("??????")
                                        .attributes(key("constraint").value("2?????? ??????")),
                                fieldWithPath("phone").description("????????????").optional(),
                                subsectionWithPath("address").description("??????").optional()

                        ),
                        requestFields(beneathPath("address"),
                                fieldWithPath("country").description("??????"),
                                fieldWithPath("address").description("??????"),
                                fieldWithPath("detail").description("????????????")
                        )
                ));
    }

    @Test
    @DisplayName("???????????? ??????:??????")
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
                .andExpect(jsonPath("$.message").value("????????? ???????????????."))
                .andExpect(jsonPath("$.validation.loginId").value("????????? ???????????? ??????????????????."))
                .andDo(document("member-join-fail",
                        responseFields(
                                fieldWithPath("code").description("?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                subsectionWithPath("validation").description("?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ??????")
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
                                fieldWithPath("id").description("?????? ID"),
                                fieldWithPath("loginId").description("????????? ID"),
                                fieldWithPath("name").description("??????"),
                                fieldWithPath("phone").description("????????????"),
                                subsectionWithPath("address").description("??????")
                        )
                ));
    }
    @Test
    @DisplayName("?????? ?????? ??????:??????")
    void test2_2() throws Exception {
        //expected

        mockMvc.perform(get("/member"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("????????? ????????? ???????????????."))
                .andDo(document("member-find-fail",
                        responseFields(
                                fieldWithPath("code").description("?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                subsectionWithPath("validation").description("?????? ??????")
                        )
                ));
    }
}
