package com.jjbin.mysite.api.controller.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbin.mysite.api.SessionConst;
import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Comment;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.repository.board.BoardRepository;
import com.jjbin.mysite.api.repository.board.CommentRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

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




    @BeforeEach
    void beforeEach(){
        boardRepository.deleteAll();
        memberRepository.deleteAll();
    }


    @Test
    @DisplayName("?????? ?????? ?????? - DB??? ?????? ????????????")
    void test1() throws Exception {
        //given

        MockHttpSession session = getMockHttpSession();
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
    void test1_2() throws Exception {
        //given

        MockHttpSession session = getMockHttpSession();
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
    void test1_3() throws Exception {
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
                .andExpect(jsonPath("$.message").value("????????? ???????????????."))
                .andExpect(jsonPath("$.validation.content").value("????????? ??????????????????."));

    }
    @Test
    @DisplayName("?????? ?????? ??????")
    void test2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
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
    void test2_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
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
    void test2_3() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
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
    void test3() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
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
    void test3_2() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
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
    void test4() throws Exception {
        //given
        MockHttpSession session = getMockHttpSession();
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