package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.repository.MailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
//@WebMvcTest
@AutoConfigureMockMvc
class MailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MailRepository mailRepository;

    @Test
    @DisplayName("메일생성테스트")
    void createMail() throws Exception {
        mockMvc.perform(post("/mail/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"제목\", \"content\": \"내용\"}")
                )
                .andExpect(status().isOk())
//                .andExpect(content().string("hello"))
                .andDo(print());
    }
    @Test
    @DisplayName("/mail 요청시 title값은 필수")
    void titleMustNotBlank() throws Exception {
        mockMvc.perform(post("/mail/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"\", \"content\": \"내용\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목을 입력해주세요."))
                .andDo(print());

        mockMvc.perform(post("/mail/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": null, \"content\": \"내용\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("id로 메일 조회 - DTO")
    void findOne() throws Exception {
        Mail mail = Mail.builder()
                .title("제목")
                .content("내용")
                .build();
        Mail save = mailRepository.save(mail);
        mockMvc.perform(get("/mail/"+save.getId()))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andDo(print());
    }

//    @Test
//    @DisplayName("id로 메일 삭제")
//    void deleteOne() throws Exception {
//        Mail mail = Mail.builder()
//                .title("제목")
//                .content("내용")
//                .build();
//        Mail save = mailRepository.save(mail);
//        Long id = save.getId();
//        mockMvc.perform(post("/mail/delete/"+id));
//
//
//
//        Assertions.assertThatThrownBy(()->mockMvc.perform(get("/mail/"+id)))
//                .isInstanceOf(IllegalArgumentException.class);
//    }

}