package com.jjbin.mysite.api.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbin.mysite.api.SessionConst;
import com.jjbin.mysite.api.domain.Address;
import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Comment;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.repository.board.BoardRepository;
import com.jjbin.mysite.api.repository.board.CommentRepository;
import com.jjbin.mysite.api.repository.board.CommentRepositoryCustom;
import com.jjbin.mysite.api.request.BoardEdit;
import com.jjbin.mysite.api.request.create.BoardCreate;
import com.jjbin.mysite.api.request.create.CommentCreate;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        MockHttpSession session = getMockHttpSession();

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

        MockHttpSession session = getMockHttpSession();
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
        mockMvc.perform(get("/board/watch/"+save.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andDo(document("board-watch",
                        responseFields(
                                fieldWithPath("id").description("게시글 ID"),
                                fieldWithPath("writer").description("작성자"),
                                fieldWithPath("title").description("글 제목"),
                                fieldWithPath("content").description("글 내용"),
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

        MockHttpSession session = getMockHttpSession();
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
                                fieldWithPath("[].createdAt").description("최초 작성일"),
                                fieldWithPath("[].lastModifiedAt").description("마지막 수정일")
                        )
                ));

    }

    @Test
    @DisplayName("게시글 삭제 요청")
    void test4() throws Exception {
        //given

        MockHttpSession session = getMockHttpSession();
        Member testMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        BoardCreate testBoardCreate = getTestBoardCreate();
        Board target = boardRepository.save(Board.createBoard(testBoardCreate, testMember));


        //when
        mockMvc.perform(delete("/board/{boardId}", target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("board-delete"));
        //then
        assertThat(boardRepository.findOne(target.getId()).orElse(null)).isNull();
    }

    @Test
    @DisplayName("게시글 삭제 요청:실패 - 존재하지 않는 게시글")
    void test4_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();

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
        MockHttpSession session = getMockHttpSession();
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
        MockHttpSession session = getMockHttpSession();

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

    @Test
    @DisplayName("댓글 작성 요청")
    void test6() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        CommentCreate commentCreate = CommentCreate.builder().content("댓글").build();

        String json = objectMapper.writeValueAsString(commentCreate);

        //when
        mockMvc.perform(post("/board/{boardId}/comment", board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("댓글"))
                .andExpect(jsonPath("$.writer").value("이름"))
                .andDo(document("comment-write",
                        requestFields(
                                fieldWithPath("content").description("내용")
                        ),
                        responseFields(
                                fieldWithPath("id").description("댓글 id"),
                                fieldWithPath("writer").description("작성자"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("createdAt").description("작성일")
                        )
                ));
        //then
        Comment comment = commentRepository.findAll().get(0);
        assertThat(comment.getContent()).isEqualTo("댓글");
    }

    @Test
    @DisplayName("댓글 작성 요청:실패")
    void test6_2() throws Exception {
        //given

        MockHttpSession session = getMockHttpSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        CommentCreate commentCreate = CommentCreate.builder().content("").build();

        String json = objectMapper.writeValueAsString(commentCreate);

        //expected
        mockMvc.perform(post("/board/{boardId}/comment", board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.content").value("댓글을 작성해주세요."))
                .andDo(document("comment-write-fail",
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메세지"),
                                subsectionWithPath("validation").description("에러 필드")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 삭제 요청")
    void test7() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        Comment save = saveTestComment(member, board,"댓글");

        //when
        mockMvc.perform(delete("/comment/{commentId}", save.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comment-delete"));


        //then
        Optional<Comment> find = commentRepository.findById(save.getId());
        assertThat(find).isEmpty();
    }
    @Test
    @DisplayName("댓글 삭제 요청:실패 - 인증되지 않은 요청")
    void test7_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        Comment save = saveTestComment(member, board,"댓글");


        //expected
        mockMvc.perform(delete("/comment/{commentId}", save.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("인증이 필요한 요청입니다."))
                .andDo(document("comment-delete-fail",
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메세지"),
                                subsectionWithPath("validation").description("에러 필드")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 리스트 조회 요청")
    void test8() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        for (int i = 0; i < 10; i++) {
            saveTestComment(member, board,"댓글"+i);
        }

        //expected
        mockMvc.perform(get("/board/{boardId}/comments?size=5&page=2",board.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].content").value("댓글4"))
                .andExpect(jsonPath("$[4].content").value("댓글0"))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].writer").isNotEmpty())
                .andDo(document("comment-list",
                        requestParameters(
                                parameterWithName("size").description("페이징할 댓글 갯수-Default 10").optional()
                                        .attributes(key("constraint").value("0이상")),
                                parameterWithName("page").description("원하는 페이지-Default 0").optional()
                                        .attributes(key("constraint").value("1이상 (1==0)"))
                        )
                        ,
                        responseFields(
                                fieldWithPath("[].id").description("댓글 ID"),
                                fieldWithPath("[].writer").description("작성자"),
                                fieldWithPath("[].content").description("내용"),
                                fieldWithPath("[].createdAt").description("작성일")                        )
                ));
    }


    private Board getTestBoard(Member member) {
        BoardCreate testBoardCreate = getTestBoardCreate();
        Board testBoard = Board.createBoard(testBoardCreate, member);
        Board board = boardRepository.save(testBoard);
        return board;
    }

    private Comment saveTestComment(Member member, Board board, String content) {
        CommentCreate commentCreate = CommentCreate.builder().content(content).build();
        Comment comment = Comment.builder()
                .member(member)
                .board(board)
                .commentCreate(commentCreate)
                .build();
        return commentRepository.save(comment);
    }



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
