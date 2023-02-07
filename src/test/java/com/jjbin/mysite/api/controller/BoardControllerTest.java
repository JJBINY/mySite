package com.jjbin.mysite.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbin.mysite.api.SessionConst;
import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.repository.board.BoardRepository;
import com.jjbin.mysite.api.request.create.BoardCreate;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void beforeEach(){
        boardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 작성 요청 - DB에 값이 저장된다")
    void test1() throws Exception {
        //given
        BoardCreate req = getTestBoardCreate();
        String json = objectMapper.writeValueAsString(req);

        MockHttpSession session = getMockHttpSession();

        //when
        mockMvc.perform(post("/board/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(print());

        //then
        Board board = boardRepository.findAll().get(0);
        assertThat(board.getTitle()).isEqualTo("제목");
        assertThat(board.getContent()).isEqualTo("내용");
    }



    @Test
    @DisplayName("게시글 작성 요청:실패 - title 필수")
    void test1_2() throws Exception {

        //given
        BoardCreate req = BoardCreate.builder()
//                .title("제목")
                .content("내용")
                .build();
        String json = objectMapper.writeValueAsString(req);

        MockHttpSession session = getMockHttpSession();

        // expected
        mockMvc.perform(post("/board/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요."))
                .andDo(print());
    }

    @DisplayName("게시글 작성 요청:실패 - 인증되지 않은 요청")
    @Test
    void test1_3() throws Exception {
        //given
        BoardCreate req = getTestBoardCreate();
        String json = objectMapper.writeValueAsString(req);

//        MockHttpSession session = getMockHttpSession();

        // expected
        mockMvc.perform(post("/board/write")
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(session)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("인증이 필요한 요청입니다."))
                .andDo(print());

    }


    @Test
    @DisplayName("게시글 조회 요청")
    void test2() throws Exception {
        //given
        BoardCreate req = getTestBoardCreate();
        String json = objectMapper.writeValueAsString(req);

        Board save = boardRepository.save(Board.createBoard(req, getTestMember()));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, save.getMember());

        // expected
        mockMvc.perform(get("/board/watch/"+save.getId())
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(session)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andDo(print());

    }


//
//    @Test
//    @DisplayName("메일 삭제 요청 - DB에서 값이 삭제된다.")
//    void test3() throws Exception {
//        // given
//        Member member = memberRepository.save(Member.builder()
//                .loginId("loginId")
//                .password("1234")
//                .name("jjbin")
//                .build()
//        );
//
//        Mail save = mailRepository.save(Mail.builder()
//                .member(member)
//                .destination("수신자")
//                .title("제목")
//                .content("내용")
//                .build());
//
//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER,member);
//
//        // when
//        mockMvc.perform(delete("/mail/"+save.getId())
//                        .session(session))
//                .andExpect(status().isOk())
//                .andDo(print());
//
//        // then
//        Mail mail = mailRepository.findById(save.getId()).orElse(null);
//        assertThat(mail).isNull();
//    }
//    @Test
//    @DisplayName("메일 삭제 요청:실패 - 인증되지 않은 요청")
//    void test3_2() throws Exception {
//        // given
//        Member member = memberRepository.save(Member.builder()
//                .loginId("loginId")
//                .password("1234")
//                .name("jjbin")
//                .build()
//        );
//
//        Mail save = mailRepository.save(Mail.builder()
//                .member(member)
//                .destination("수신자")
//                .title("제목")
//                .content("내용")
//                .build());
//
//
//        // expected
//        mockMvc.perform(delete("/mail/"+save.getId()))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.code").value("401"))
//                .andExpect(jsonPath("$.message").value("인증이 필요한 요청입니다."))
//                .andDo(print());
//
//    }
//
//    @Test
//    @DisplayName("메일 삭제 요청:실패 - 존재하지 않는 메일")
//    void test3_3() throws Exception {
//        // given
//        Member member = memberRepository.save(Member.builder()
//                .loginId("loginId")
//                .password("1234")
//                .name("jjbin")
//                .build()
//        );
//
//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER,member);
//
//        // expected
//        mockMvc.perform(delete("/mail/{mailId}",1L)
//                        .session(session))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.code").value("404"))
//                .andExpect(jsonPath("$.message").value("존재하지 않는 객체입니다."))
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("메일 여러개 조회 요청 - 파라미터를 넘기지 않으면 디폴트 값이 할당된다.")
//    void test4() throws Exception {
//        // given
//        Member member = memberRepository.save(Member.builder()
//                .loginId("loginId")
//                .password("1234")
//                .name("jjbin")
//                .build()
//        );
//
//        List<Mail> mails = IntStream.range(0, 20)
//                .mapToObj(i -> Mail.builder()
//                        .destination("수신자" + i)
//                        .title("제목" + i)
//                        .content("내용" + i)
//                        .member(member)
//                        .build())
//                .collect(Collectors.toList());
//        mailRepository.saveAll(mails);
//
//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER,member);
//
//        // expected
//        mockMvc.perform(get("/mail/list")
//                        .session(session))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(10))
//                .andExpect(jsonPath("$[0].destination").value("수신자19"))
//                .andExpect(jsonPath("$[0].title").value("제목19"))
//                .andExpect(jsonPath("$[0].content").value("내용19"))
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("메일 여러개 조회 요청 - page=0또는1 을 요청하면 첫 번째 페이지를 가져온다.")
//    void test4_2() throws Exception {
//        // given
//        Member member = memberRepository.save(Member.builder()
//                .loginId("loginId")
//                .password("1234")
//                .name("jjbin")
//                .build()
//        );
//
//        List<Mail> mails = IntStream.range(0, 20)
//                .mapToObj(i -> Mail.builder()
//                        .destination("수신자" + i)
//                        .title("제목" + i)
//                        .content("내용" + i)
//                        .member(member)
//                        .build())
//                .collect(Collectors.toList());
//        mailRepository.saveAll(mails);
//
//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER, member);
//
//        // expected
//        mockMvc.perform(get("/mail/list?page=0")
//                        .session(session))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(10))
//                .andExpect(jsonPath("$[0].destination").value("수신자19"))
//                .andExpect(jsonPath("$[0].title").value("제목19"))
//                .andExpect(jsonPath("$[0].content").value("내용19"))
//                .andDo(print());
//
//        mockMvc.perform(get("/mail/list?page=1")
//                        .session(session))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(10))
//                .andExpect(jsonPath("$[0].destination").value("수신자19"))
//                .andExpect(jsonPath("$[0].title").value("제목19"))
//                .andExpect(jsonPath("$[0].content").value("내용19"))
//                .andDo(print());
//    }
//    @Test
//    @DisplayName("메일 여러개 조회 요청 - page=2을 요청하면 두 번째 페이지를 가져온다.")
//    void test4_3() throws Exception {
//        // given
//        Member member = memberRepository.save(Member.builder()
//                .loginId("loginId")
//                .password("1234")
//                .name("jjbin")
//                .build()
//        );
//
//        List<Mail> mails = IntStream.range(0, 20)
//                .mapToObj(i -> Mail.builder()
//                        .destination("수신자" + i)
//                        .title("제목" + i)
//                        .content("내용" + i)
//                        .member(member)
//                        .build())
//                .collect(Collectors.toList());
//        mailRepository.saveAll(mails);
//
//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER, member);
//
//        // expected
//        mockMvc.perform(get("/mail/list?page=2")
//                        .session(session))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(10))
//                .andExpect(jsonPath("$[0].destination").value("수신자9"))
//                .andExpect(jsonPath("$[0].title").value("제목9"))
//                .andExpect(jsonPath("$[0].content").value("내용9"))
//                .andDo(print());
//    }
//    @Test
//    @DisplayName("메일 여러개 조회 요청 - size 개수만큼 조회한다.")
//    void test4_4() throws Exception {
//        // given
//        Member member = memberRepository.save(Member.builder()
//                .loginId("loginId")
//                .password("1234")
//                .name("jjbin")
//                .build()
//        );
//
//        List<Mail> mails = IntStream.range(0, 20)
//                .mapToObj(i -> Mail.builder()
//                        .destination("수신자" + i)
//                        .title("제목" + i)
//                        .content("내용" + i)
//                        .member(member)
//                        .build())
//                .collect(Collectors.toList());
//        mailRepository.saveAll(mails);
//
//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER, member);
//
//        // expected
//        mockMvc.perform(get("/mail/list?size=20")
//                        .session(session))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(20))
//                .andExpect(jsonPath("$[0].destination").value("수신자19"))
//                .andExpect(jsonPath("$[0].title").value("제목19"))
//                .andExpect(jsonPath("$[0].content").value("내용19"))
//                .andDo(print());
//    }
//
//
    private MockHttpSession getMockHttpSession() {

        Member member = getTestMember();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);
        return session;
    }

    private static BoardCreate getTestBoardCreate() {
        return BoardCreate.builder()
                .title("제목")
                .content("내용")
                .build();
    }

    private Member getTestMember() {
        return memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );
    }
}