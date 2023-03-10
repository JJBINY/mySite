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
    @DisplayName("게시글 작성 요청")
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
                                fieldWithPath("title").description("글 제목"),
                                fieldWithPath("content").description("글 내용").optional()
                        )
                ));
    }

    @Test
    @DisplayName("게시글 작성 요청:실패")
    void test1_2() throws Exception {
        //given
        BoardCreate req = BoardCreate.builder()
                .title("")
                .content("내용")
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
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요."))
                .andDo(document("board-write-fail",
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메세지"),
                                subsectionWithPath("validation").description("에러 필드")
                        )
                ));
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
        mockMvc.perform(RestDocumentationRequestBuilders.get("/board/watch/{boardId}",save.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andDo(document("board-watch",
                        pathParameters(
                          parameterWithName("boardId").description("게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("게시글 ID"),
                                fieldWithPath("writer").description("작성자"),
                                fieldWithPath("title").description("글 제목"),
                                fieldWithPath("content").description("글 내용"),
                                fieldWithPath("likes").description("좋아요 갯수"),
                                fieldWithPath("createdAt").description("최초 작성일"),
                                fieldWithPath("lastModifiedAt").description("마지막 수정일")
                        )
                ));
    }

    @Test
    @DisplayName("게시글 리스트 조회 요청")
    void test3() throws Exception {
        // given
        Member writer = getTestMember();
        List<Board> boardList = IntStream.range(0, 20)
                .mapToObj(i ->
                        Board.createBoard(
                                BoardCreate.builder()
                                        .title("제목" + i)
                                        .content("내용" + i)
                                        .build(), writer)
                ).collect(Collectors.toList());
        boardRepository.saveAll(boardList);

        MockHttpSession session = getMockHttpSession(writer);
        // expected
        mockMvc.perform(get("/board/list?size=10&page=1&keyword=제목")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].title").value("제목19"))
                .andExpect(jsonPath("$[0].content").value("내용19"))
                .andDo(print())
                .andDo(document("board-list",
                        requestParameters(
                                parameterWithName("size").description("페이징할 게시글 갯수-Default 10").optional()
                                        .attributes(key("constraint").value("0이상")),
                                parameterWithName("page").description("원하는 페이지-Default 0").optional()
                                        .attributes(key("constraint").value("1이상 (1==0)")),
                                parameterWithName("keyword").description("제목 검색 키워드").optional()
                        )
                        ,
                        responseFields(
                                fieldWithPath("[].id").description("게시글 ID"),
                                fieldWithPath("[].writer").description("작성자"),
                                fieldWithPath("[].title").description("글 제목"),
                                fieldWithPath("[].content").description("글 내용"),
                                fieldWithPath("[].likes").description("좋아요 갯수"),
                                fieldWithPath("[].createdAt").description("최초 작성일"),
                                fieldWithPath("[].lastModifiedAt").description("마지막 수정일")
                        )
                ));

    }

    @Test
    @DisplayName("게시글 삭제 요청")
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
                                parameterWithName("boardId").description("게시글 ID")
                        )
                ));
        //then
        assertThat(boardRepository.findOne(target.getId()).orElse(null)).isNull();
    }

    @Test
    @DisplayName("게시글 삭제 요청:실패 - 존재하지 않는 게시글")
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
                .andExpect(jsonPath("$.message").value("존재하지 않는 객체입니다."))
                .andExpect(jsonPath("$.validation").value(""))
                .andDo(print())
                .andDo(document("board-delete-fail",
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메세지"),
                                subsectionWithPath("validation").description("에러 필드")
                        )
                ));
    }

    @Test
    @DisplayName("게시글 수정 요청")
    void test5() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
        Member testMember= (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        BoardCreate testBoardCreate = getTestBoardCreate();
        Board target = boardRepository.save(Board.createBoard(testBoardCreate, testMember));

        BoardEdit edit = BoardEdit.builder()
                .title("수정제목")
                .content("수정내용")
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
                                parameterWithName("boardId").description("게시글 ID")
                        ),
                        requestFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용").optional()
                        )
                        ));

        //then
        Board board = boardRepository.findOne(target.getId()).get();
        assertThat(board.getTitle()).isEqualTo("수정제목");
        assertThat(board.getContent()).isEqualTo("수정내용");
        assertThat(board.getLastModifiedAt()).isNotEqualTo(target.getLastModifiedAt());
    }

    @Test
    @DisplayName("게시글 수정 요청:실패 - 존재하지 않는 게시글")
    void test5_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());

        BoardEdit edit = BoardEdit.builder()
                .title("수정제목")
                .content("수정내용")
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
                .andExpect(jsonPath("$.message").value("존재하지 않는 객체입니다."))
                .andExpect(jsonPath("$.validation").value(""))
                .andDo(document("board-edit-fail",
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메세지"),
                                subsectionWithPath("validation").description("에러 필드")
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
