package com.jjbin.mysite.api.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbin.mysite.api.SessionConst;
import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.repository.board.BoardRepository;
import com.jjbin.mysite.api.repository.board.CommentRepository;
import com.jjbin.mysite.api.request.BoardEdit;
import com.jjbin.mysite.api.request.create.BoardCreate;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class BoardDocTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        memberRepository.deleteAll();
        boardRepository.deleteAll();
        commentRepository.deleteAll();
    }


    @Test
    @DisplayName("????????? ?????? ??????")
    void test1() throws Exception {
        //given
        BoardCreate req = getTestBoardCreate();
        String json = objectMapper.writeValueAsString(req);

        MockHttpSession session = getMockHttpSession(getTestMember());

        // expected
        mockMvc.perform(RestDocumentationRequestBuilders.post("/board/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("board-write",
                        requestFields(
                                fieldWithPath("title").description("??? ??????"),
                                fieldWithPath("content").description("??? ??????").optional()
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ??????:??????")
    void test1_2() throws Exception {
        //given
        BoardCreate req = BoardCreate.builder()
                .title("")
                .content("??????")
                .build();
        String json = objectMapper.writeValueAsString(req);

        MockHttpSession session = getMockHttpSession(getTestMember());
        // expected
        mockMvc.perform(RestDocumentationRequestBuilders.post("/board/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("????????? ???????????????."))
                .andExpect(jsonPath("$.validation.title").value("????????? ??????????????????."))
                .andDo(document("board-write-fail",
                        responseFields(
                                fieldWithPath("code").description("?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                subsectionWithPath("validation").description("?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void test2() throws Exception {
        //given
        BoardCreate req = getTestBoardCreate();
        String json = objectMapper.writeValueAsString(req);

        Board save = boardRepository.save(Board.createBoard(req, getTestMember()));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, save.getMember());

        // expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/board/watch/{boardId}",save.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("??????"))
                .andExpect(jsonPath("$.content").value("??????"))
                .andDo(document("board-watch",
                        pathParameters(
                          parameterWithName("boardId").description("????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("????????? ID"),
                                fieldWithPath("writer").description("?????????"),
                                fieldWithPath("title").description("??? ??????"),
                                fieldWithPath("content").description("??? ??????"),
                                fieldWithPath("likes").description("????????? ??????"),
                                fieldWithPath("createdAt").description("?????? ?????????"),
                                fieldWithPath("lastModifiedAt").description("????????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ????????? ?????? ??????")
    void test3() throws Exception {
        // given
        Member writer = getTestMember();
        List<Board> boardList = IntStream.range(0, 20)
                .mapToObj(i ->
                        Board.createBoard(
                                BoardCreate.builder()
                                        .title("??????" + i)
                                        .content("??????" + i)
                                        .build(), writer)
                ).collect(Collectors.toList());
        boardRepository.saveAll(boardList);

        MockHttpSession session = getMockHttpSession(writer);
        // expected
        mockMvc.perform(get("/board/list?size=10&page=1&keyword=??????")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].title").value("??????19"))
                .andExpect(jsonPath("$[0].content").value("??????19"))
                .andDo(print())
                .andDo(document("board-list",
                        requestParameters(
                                parameterWithName("size").description("???????????? ????????? ??????-Default 10").optional()
                                        .attributes(key("constraint").value("0??????")),
                                parameterWithName("page").description("????????? ?????????-Default 0").optional()
                                        .attributes(key("constraint").value("1?????? (1==0)")),
                                parameterWithName("keyword").description("?????? ?????? ?????????").optional()
                        )
                        ,
                        responseFields(
                                fieldWithPath("[].id").description("????????? ID"),
                                fieldWithPath("[].writer").description("?????????"),
                                fieldWithPath("[].title").description("??? ??????"),
                                fieldWithPath("[].content").description("??? ??????"),
                                fieldWithPath("[].likes").description("????????? ??????"),
                                fieldWithPath("[].createdAt").description("?????? ?????????"),
                                fieldWithPath("[].lastModifiedAt").description("????????? ?????????")
                        )
                ));

    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void test4() throws Exception {
        //given

        MockHttpSession session = getMockHttpSession(getTestMember());
        Member testMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        BoardCreate testBoardCreate = getTestBoardCreate();
        Board target = boardRepository.save(Board.createBoard(testBoardCreate, testMember));


        //when
        mockMvc.perform(delete("/board/{boardId}", target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("board-delete",
                        pathParameters(
                                parameterWithName("boardId").description("????????? ID")
                        )
                ));
        //then
        assertThat(boardRepository.findOne(target.getId()).orElse(null)).isNull();
    }

    @Test
    @DisplayName("????????? ?????? ??????:?????? - ???????????? ?????? ?????????")
    void test4_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());

        //expected
        mockMvc.perform(delete("/board/{boardId}", 100)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("???????????? ?????? ???????????????."))
                .andExpect(jsonPath("$.validation").value(""))
                .andDo(print())
                .andDo(document("board-delete-fail",
                        responseFields(
                                fieldWithPath("code").description("?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                subsectionWithPath("validation").description("?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void test5() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
        Member testMember= (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        BoardCreate testBoardCreate = getTestBoardCreate();
        Board target = boardRepository.save(Board.createBoard(testBoardCreate, testMember));

        BoardEdit edit = BoardEdit.builder()
                .title("????????????")
                .content("????????????")
                .build();

        String json = objectMapper.writeValueAsString(edit);

        //when
        mockMvc.perform(patch("/board/{boardId}", target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("board-edit",
                        pathParameters(
                                parameterWithName("boardId").description("????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("title").description("??????"),
                                fieldWithPath("content").description("??????").optional()
                        )
                        ));

        //then
        Board board = boardRepository.findOne(target.getId()).get();
        assertThat(board.getTitle()).isEqualTo("????????????");
        assertThat(board.getContent()).isEqualTo("????????????");
        assertThat(board.getLastModifiedAt()).isNotEqualTo(target.getLastModifiedAt());
    }

    @Test
    @DisplayName("????????? ?????? ??????:?????? - ???????????? ?????? ?????????")
    void test5_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());

        BoardEdit edit = BoardEdit.builder()
                .title("????????????")
                .content("????????????")
                .build();

        String json = objectMapper.writeValueAsString(edit);

        //expected
        mockMvc.perform(patch("/board/{boardId}", 100)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("???????????? ?????? ???????????????."))
                .andExpect(jsonPath("$.validation").value(""))
                .andDo(document("board-edit-fail",
                        responseFields(
                                fieldWithPath("code").description("?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                subsectionWithPath("validation").description("?????? ??????")
                        )
                ));
    }








    private MockHttpSession getMockHttpSession(Member member) {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);
        return session;
    }

    private static BoardCreate getTestBoardCreate() {
        return BoardCreate.builder()
                .title("??????")
                .content("??????")
                .build();
    }

    private Member getTestMember() {
        return memberRepository.save(
                Member.builder()
                        .name("??????")
                        .loginId("?????????")
                        .build()
        );
    }
}
