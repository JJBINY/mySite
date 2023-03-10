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
    @DisplayName("????????? ?????? ?????? - DB??? ?????? ????????????")
    void test1() throws Exception {
        //given
        BoardCreate req = getTestBoardCreate();
        String json = objectMapper.writeValueAsString(req);

        MockHttpSession session = getMockHttpSession(getTestMember());

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
        assertThat(board.getTitle()).isEqualTo("??????");
        assertThat(board.getContent()).isEqualTo("??????");
    }



    @Test
    @DisplayName("????????? ?????? ??????:?????? - title ??????")
    void test1_2() throws Exception {

        //given
        BoardCreate req = BoardCreate.builder()
//                .title("??????")
                .content("??????")
                .build();
        String json = objectMapper.writeValueAsString(req);

        MockHttpSession session = getMockHttpSession(getTestMember());

        // expected
        mockMvc.perform(post("/board/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("????????? ???????????????."))
                .andExpect(jsonPath("$.validation.title").value("????????? ??????????????????."))
                .andDo(print());
    }

    @DisplayName("????????? ?????? ??????:?????? - ???????????? ?????? ??????")
    @Test
    void test1_3() throws Exception {
        //given
        BoardCreate req = getTestBoardCreate();
        String json = objectMapper.writeValueAsString(req);

//        MockHttpSession session = getMockHttpSession(getTestMember());

        // expected
        mockMvc.perform(post("/board/write")
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(session)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("????????? ????????? ???????????????."))
                .andDo(print());

    }


    @Test
    @DisplayName("????????? ?????? ??????")
    void test2() throws Exception {
        //given
        Board save = boardRepository.save(Board.createBoard(getTestBoardCreate(), getTestMember()));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, save.getMember());

        // expected
        mockMvc.perform(get("/board/watch/"+save.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("??????"))
                .andExpect(jsonPath("$.content").value("??????"))
                .andDo(print());

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
        mockMvc.perform(get("/board/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].title").value("??????19"))
                .andExpect(jsonPath("$[0].content").value("??????19"))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ????????? ?????? ?????? - ?????? ???????????? ??????")
    void test3_1() throws Exception {
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
        mockMvc.perform(get("/board/list?page=2&size=5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].title").value("??????14"))
                .andExpect(jsonPath("$[0].content").value("??????14"))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ????????? ?????? ?????? - ??? ?????? ??????")
    void test3_2() throws Exception {
        // given
        Member writer = getTestMember();
        List<Board> boardList = IntStream.range(0, 5)
                .mapToObj(i ->
                        Board.createBoard(
                                BoardCreate.builder()
                                        .title("??????" + i)
                                        .content("??????" + i)
                                        .build(), writer)
                ).collect(Collectors.toList());
        List<Board> boardList2 = IntStream.range(0, 3)
                .mapToObj(i ->
                        Board.createBoard(
                                BoardCreate.builder()
                                        .title("?????? ?????????" + i)
                                        .content("??????" + i)
                                        .build(), writer)
                ).collect(Collectors.toList());
        boardRepository.saveAll(boardList);
        boardRepository.saveAll(boardList2);

        MockHttpSession session = getMockHttpSession(writer);


        // expected
        mockMvc.perform(get("/board/list?size=5&page=1&keyword=?????????")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("?????? ?????????2"))
                .andExpect(jsonPath("$[0].content").value("??????2"))
                .andDo(print());
    }
    @Test
    @DisplayName("????????? ?????? ?????? - DB?????? ?????? ????????????")
    void test4() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
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
    @DisplayName("????????? ?????? ??????:?????? - ???????????? ?????? ?????????")
    void test4_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
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
                .andExpect(jsonPath("$.message").value("???????????? ?????? ???????????????."))
                .andExpect(jsonPath("$.validation").value(""))
                .andDo(print());

    }
    @Test
    @DisplayName("????????? ?????? ??????:?????? - ???????????? ?????? ??????")
    void test4_3() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
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
                .andExpect(jsonPath("$.message").value("????????? ????????? ???????????????."))
                .andExpect(jsonPath("$.validation").value(""))
                .andDo(print());

    }
    @Test
    @DisplayName("????????? ?????? ?????? - DB?????? ?????? ????????????")
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
                .andExpect(status().isOk());

        //then
        Board board = boardRepository.findOne(target.getId()).get();
        assertThat(board.getTitle()).isEqualTo("????????????");
        assertThat(board.getContent()).isEqualTo("????????????");
        assertThat(board.getLastModifiedAt()).isNotEqualTo(target.getLastModifiedAt());
    }
    @Test
    @DisplayName("????????? ?????? ??????:?????? - ????????? ????????? ??????")
    void test5_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
        Member testMember= (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        BoardCreate testBoardCreate = getTestBoardCreate();
        Board target = boardRepository.save(Board.createBoard(testBoardCreate, testMember));

        BoardEdit edit = BoardEdit.builder()
                .title("")
                .content("????????????")
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
                .andExpect(jsonPath("$.message").value("????????? ???????????????."))
                .andExpect(jsonPath("$.validation.title").value("????????? ??????????????????."));


    }
    @Test
    @DisplayName("????????? ?????? ??????:?????? - ???????????? ?????? ?????????")
    void test5_3() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
//        Member testMember= (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
//
//        BoardCreate testBoardCreate = getTestBoardCreate();
//        Board target = boardRepository.save(Board.createBoard(testBoardCreate, testMember));

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
                .andDo(print());

    }
    @Test
    @DisplayName("????????? ?????? ??????:?????? - ???????????? ?????? ??????")
    void test5_4() throws Exception {
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

        //expected
        mockMvc.perform(patch("/board/{boardId}", target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
//                        .session(session)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("????????? ????????? ???????????????."))
                .andExpect(jsonPath("$.validation").value(""))
                .andDo(print());

    }
    @Test
    @DisplayName("?????? ?????? ?????? - DB??? ?????? ????????????")
    void test6() throws Exception {
        //given

        MockHttpSession session = getMockHttpSession(getTestMember());
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        CommentCreate commentCreate = CommentCreate.builder().content("??????").build();

        String json = objectMapper.writeValueAsString(commentCreate);

        //when
        mockMvc.perform(post("/board/{boardId}/comment", board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("??????"))
                .andExpect(jsonPath("$.writer").value("??????"));
        //then
        Comment comment = commentRepository.findAll().get(0);
        assertThat(comment.getContent()).isEqualTo("??????");
    }
    @Test
    @DisplayName("?????? ?????? ??????:?????? - ???????????? ?????? ??????")
    void test6_2() throws Exception {
        //given

        MockHttpSession session = getMockHttpSession(getTestMember());
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        CommentCreate commentCreate = CommentCreate.builder().content("??????").build();

        String json = objectMapper.writeValueAsString(commentCreate);

        //expected
        mockMvc.perform(post("/board/{boardId}/comment", board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(session)
                        .content(json))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("????????? ????????? ???????????????."));
    }
    @Test
    @DisplayName("?????? ?????? ??????:?????? - content??? ??????")
    void test6_3() throws Exception {
        //given

        MockHttpSession session = getMockHttpSession(getTestMember());
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
                .andExpect(jsonPath("$.message").value("????????? ???????????????."))
                .andExpect(jsonPath("$.validation.content").value("????????? ??????????????????."));

    }
    @Test
    @DisplayName("?????? ?????? ??????")
    void test7() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        Comment save = saveTestComment(member, board,"??????");

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
    @DisplayName("?????? ?????? ??????:?????? - ???????????? ?????? ??????")
    void test7_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        Comment save = saveTestComment(member, board,"??????");


        //expected
        mockMvc.perform(delete("/comment/{commentId}", save.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("????????? ????????? ???????????????."));
    }
    @Test
    @DisplayName("?????? ?????? ??????:?????? - ???????????? ?????? ??????")
    void test7_3() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        Comment save = saveTestComment(member, board,"??????");

        //expected
        mockMvc.perform(delete("/comment/{commentId}", save.getId()+1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("???????????? ?????? ???????????????."));
    }
    @Test
    @DisplayName("?????? ????????? ?????? ??????")
    void test8() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        for (int i = 0; i < 10; i++) {
            saveTestComment(member, board,"??????"+i);
        }

        //expected
        mockMvc.perform(get("/board/{boardId}/comments?size=5&page=2",board.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].content").value("??????4"))
                .andExpect(jsonPath("$[4].content").value("??????0"))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].writer").isNotEmpty());
    }
    @Test
    @DisplayName("?????? ????????? ?????? ?????? - size&page ??????????????? ????????? ????????? ????????? ?????? ????????????")
    void test8_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        for (int i = 0; i < 10; i++) {
            saveTestComment(member, board,"??????"+i);
        }

        //expected
        mockMvc.perform(get("/board/{boardId}/comments", board.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].content").value("??????9"));
    }
    @Test
    @DisplayName("?????? ?????? ????????? ?????? ??????")
    void test9() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        Comment parent = saveTestComment(member, board, "??????");

        for (int i = 0; i < 10; i++) {
            CommentCreate commentCreate = CommentCreate.builder()
                    .content("??????"+i)
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
                .andExpect(jsonPath("$[0].content").value("??????4"))
                .andExpect(jsonPath("$[4].content").value("??????0"));
    }

    @Test
    @DisplayName("????????? ????????? ?????? - db??? ????????? ????????? ????????????.")
    void test10() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
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
    @DisplayName("????????? ????????? ?????? - ?????? ???????????? ?????? ???????????? ????????????.")
    void test10_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
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
    @DisplayName("????????? ????????? ??????:?????? - ???????????? ?????? ??????")
    void test10_3() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession(getTestMember());
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Board board = getTestBoard(member);

        //expected
        mockMvc.perform(post("/board/{boardId}/like", board.getId()))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("????????? ????????? ???????????????."));


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