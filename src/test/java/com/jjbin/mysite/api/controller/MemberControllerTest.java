package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.domain.Address;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    @DisplayName("join 요청")
    void join() throws Exception {
        long count = memberRepository.count();

        mockMvc.perform(post("/member/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\" : \"testName\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testName"))
                .andDo(print());

        Assertions.assertThat(memberRepository.count()).isEqualTo(count+1);
    }

    @Test
    @DisplayName("Member 단건 조회")
    void findOne() throws Exception {
        //given
        Member member = Member.builder()
                .name("test")
                .phone("010-1234-1234")
                .address(new Address("seoul", "myeonmok-ro", "123"))
                .build();
        Member saveMember = memberRepository.save(member);

        //expected
        mockMvc.perform(get("/member/" + saveMember.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.phone").value("010-1234-1234"))
                .andExpect(jsonPath("$.address.city").value("seoul"))
                .andDo(print());
    }
}