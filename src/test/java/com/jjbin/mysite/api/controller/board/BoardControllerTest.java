package com.jjbin.mysite.api.controller.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbin.mysite.api.SessionConst;
import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Comment;
import com.jjbin.mysite.api.domain.Like;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.repository.board.CommentRepository;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.repository.board.BoardRepository;
import com.jjbin.mysite.api.repository.board.LikeRepository;
import com.jjbin.mysite.api.request.BoardEdit;
import com.jjbin.mysite.api.request.create.BoardCreate;
import com.jjbin.mysite.api.request.create.CommentCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikeRepository likeRepository;


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
        Board save = boardRepository.save(Board.createBoard(getTestBoardCreate(), getTestMember()));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, save.getMember());

        // expected
        mockMvc.perform(get("/board/watch/"+save.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andDo(print());

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
        mockMvc.perform(get("/board/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].title").value("제목19"))
                .andExpect(jsonPath("$[0].content").value("내용19"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 리스트 조회 요청 - 검색 파라미터 설정")
    void test3_1() throws Exception {
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
        mockMvc.perform(get("/board/list?page=2&size=5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].title").value("제목14"))
                .andExpect(jsonPath("$[0].content").value("내용14"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 리스트 조회 요청 - 글 제목 검색")
    void test3_2() throws Exception {
        // given
        Member writer = getTestMember();
        List<Board> boardList = IntStream.range(0, 5)
                .mapToObj(i ->
                        Board.createBoard(
                                BoardCreate.builder()
                                        .title("제목" + i)
                                        .content("내용" + i)
                                        .build(), writer)
                ).collect(Collectors.toList());
        List<Board> boardList2 = IntStream.range(0, 3)
                .mapToObj(i ->
                        Board.createBoard(
                                BoardCreate.builder()
                                        .title("제목 검색어" + i)
                                        .content("내용" + i)
                                        .build(), writer)
                ).collect(Collectors.toList());
        boardRepository.saveAll(boardList);
        boardRepository.saveAll(boardList2);

        MockHttpSession session = getMockHttpSession();


        // expected
        mockMvc.perform(get("/board/list?size=5&page=1&keyword=검색어")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("제목 검색어2"))
                .andExpect(jsonPath("$[0].content").value("내용2"))
                .andDo(print());
    }
    @Test
    @DisplayName("게시글 삭제 요청 - DB에서 값이 삭제된다")
    void test4() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
        Member testMember= (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        BoardCreate testBoardCreate = getTestBoardCreate();
        Board target = boardRepository.save(Board.createBoard(testBoardCreate, testMember));


        //when
        mockMvc.perform(delete("/board/{boardId}", target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        assertThat(boardRepository.findOne(target.getId()).orElse(null)).isNull();
    }
    @Test
    @DisplayName("게시글 삭제 요청:실패 - 존재하지 않는 게시글")
    void test4_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
//        Member testMember= (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
//
//        BoardCreate testBoardCreate = getTestBoardCreate();
//        Board target = boardRepository.save(Board.createBoard(testBoardCreate, testMember));


        //expected
        mockMvc.perform(delete("/board/{boardId}", 100)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 객체입니다."))
                .andExpect(jsonPath("$.validation").value(""))
                .andDo(print());

    }
    @Test
    @DisplayName("게시글 삭제 요청:실패 - 인증되지 않은 요청")
    void test4_3() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
        Member testMember= (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        BoardCreate testBoardCreate = getTestBoardCreate();
        Board target = boardRepository.save(Board.createBoard(testBoardCreate, testMember));


        //expected
        mockMvc.perform(delete("/board/{boardId}", target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(session)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("인증이 필요한 요청입니다."))
                .andExpect(jsonPath("$.validation").value(""))
                .andDo(print());

    }
    @Test
    @DisplayName("게시글 수정 요청 - DB에서 값이 수정된다")
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
                .andExpect(status().isOk());

        //then
        Board board = boardRepository.findOne(target.getId()).get();
        assertThat(board.getTitle()).isEqualTo("수정제목");
        assertThat(board.getContent()).isEqualTo("수정내용");
        assertThat(board.getLastModifiedAt()).isNotEqualTo(target.getLastModifiedAt());
    }
    @Test
    @DisplayName("게시글 수정 요청:실패 - 수정시 제목은 필수")
    void test5_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
        Member testMember= (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        BoardCreate testBoardCreate = getTestBoardCreate();
        Board target = boardRepository.save(Board.createBoard(testBoardCreate, testMember));

        BoardEdit edit = BoardEdit.builder()
                .title("")
                .content("수정내용")
                .build();

        String json = objectMapper.writeValueAsString(edit);

        //expected
        mockMvc.perform(patch("/board/{boardId}", target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요."));


    }
    @Test
    @DisplayName("게시글 수정 요청:실패 - 존재하지 않는 게시글")
    void test5_3() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
//        Member testMember= (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
//
//        BoardCreate testBoardCreate = getTestBoardCreate();
//        Board target = boardRepository.save(Board.createBoard(testBoardCreate, testMember));

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
                .andDo(print());

    }
    @Test
    @DisplayName("게시글 수정 요청:실패 - 인증되지 않은 요청")
    void test5_4() throws Exception {
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

        //expected
        mockMvc.perform(patch("/board/{boardId}", target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
//                        .session(session)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("인증이 필요한 요청입니다."))
                .andExpect(jsonPath("$.validation").value(""))
                .andDo(print());

    }
    @Test
    @DisplayName("댓글 작성 요청 - DB에 값이 저장된다")
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
                .andExpect(jsonPath("$.writer").value("이름"));
        //then
        Comment comment = commentRepository.findAll().get(0);
        assertThat(comment.getContent()).isEqualTo("댓글");
    }
    @Test
    @DisplayName("댓글 작성 요청:실패 - 인증되지 않은 요청")
    void test6_2() throws Exception {
        //given

        MockHttpSession session = getMockHttpSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        CommentCreate commentCreate = CommentCreate.builder().content("댓글").build();

        String json = objectMapper.writeValueAsString(commentCreate);

        //expected
        mockMvc.perform(post("/board/{boardId}/comment", board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(session)
                        .content(json))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("인증이 필요한 요청입니다."));
    }
    @Test
    @DisplayName("댓글 작성 요청:실패 - content는 필수")
    void test6_3() throws Exception {
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
                .andExpect(jsonPath("$.validation.content").value("댓글을 작성해주세요."));

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
                .andExpect(status().isOk());

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
                .andExpect(jsonPath("$.message").value("인증이 필요한 요청입니다."));
    }
    @Test
    @DisplayName("댓글 삭제 요청:실패 - 존재하지 않는 댓글")
    void test7_3() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        Comment save = saveTestComment(member, board,"댓글");

        //expected
        mockMvc.perform(delete("/comment/{commentId}", save.getId()+1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 객체입니다."));
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
                .andExpect(jsonPath("$[0].writer").isNotEmpty());
    }
    @Test
    @DisplayName("댓글 리스트 조회 요청 - size&page 파라미터를 넘기지 않으면 디폴트 값이 할당된다")
    void test8_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        for (int i = 0; i < 10; i++) {
            saveTestComment(member, board,"댓글"+i);
        }

        //expected
        mockMvc.perform(get("/board/{boardId}/comments", board.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].content").value("댓글9"));
    }
    @Test
    @DisplayName("자식 댓글 리스트 조회 요청")
    void test9() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        Comment parent = saveTestComment(member, board, "부모");

        for (int i = 0; i < 10; i++) {
            CommentCreate commentCreate = CommentCreate.builder()
                    .content("자식"+i)
//                    .parentId(parent.getId())
                    .build();
            Comment comment = Comment.builder()
                    .member(member)
                    .board(board)
                    .commentCreate(commentCreate)
                    .parent(parent)
                    .build();
            commentRepository.save(comment);
        }

        //expected
        mockMvc.perform(get("/comment/{commentId}/children?size=5&page=2", parent.getId())
                        .session(session)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].content").value("자식4"))
                .andExpect(jsonPath("$[4].content").value("자식0"));
    }

    @Test
    @DisplayName("게시글 좋아요 요청 - db에 좋아요 정보가 저장된다.")
    void test10() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        //expected
        mockMvc.perform(post("/board/{boardId}/like", board.getId())
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));

    }
    @Test
    @DisplayName("게시글 좋아요 요청 - 이미 좋아요한 경우 좋아요가 취소된다.")
    void test10_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        //when
        likeRepository.save(
                Like.builder()
                        .member(member)
                        .board(board)
                        .build()
        );
        //then
        mockMvc.perform(post("/board/{boardId}/like", board.getId())
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0));

    }

    @Test
    @DisplayName("게시글 좋아요 요청:실패 - 인증되지 않은 요청")
    void test10_3() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        //expected
        mockMvc.perform(post("/board/{boardId}/like", board.getId()))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("인증이 필요한 요청입니다."));


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