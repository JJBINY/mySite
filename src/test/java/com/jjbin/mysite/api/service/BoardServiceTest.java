package com.jjbin.mysite.api.service;

import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.repository.board.BoardRepository;
import com.jjbin.mysite.api.request.BoardEdit;
import com.jjbin.mysite.api.request.SearchOption;
import com.jjbin.mysite.api.request.create.BoardCreate;
import com.jjbin.mysite.api.service.board.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class BoardServiceTest {

    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;



    @BeforeEach
    void beforeEach() {
        boardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 작성")
    void test1() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        BoardCreate boardCreate = BoardCreate.builder()
                .title("제목")
                .content("내용")
                .build();

        Board createdBoard = Board.createBoard(boardCreate, member);
        long count = boardRepository.count();

        // when
        Long saveId = boardService.write(createdBoard);
        Board board = boardRepository.findOne(saveId).orElse(null);

        // then
        assertThat(boardRepository.count()).isEqualTo(count + 1);
        assertThat(board.getTitle()).isEqualTo("제목");
        assertThat(board.getContent()).isEqualTo("내용");
        assertThat(board.getMember().getId()).isEqualTo(member.getId());
        assertThat(board.getMember().getName()).isEqualTo("이름");
        assertThat(board.getMember().getLoginId()).isEqualTo("아이디");
    }


    @Test
    @DisplayName("게시글 1개 조회")
    void test2() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        BoardCreate boardCreate = BoardCreate.builder()
                .title("제목")
                .content("내용")
                .build();

        Board board = boardRepository.save(Board.createBoard(boardCreate, member));

        //when
        Board findOne = boardService.findOne(board.getId());

        //then
        assertThat(findOne).isNotNull();
        assertThat(findOne.getTitle()).isEqualTo("제목");
        assertThat(findOne.getContent()).isEqualTo("내용");
        assertThat(findOne.getMember().getId()).isEqualTo(member.getId());
        assertThat(findOne.getMember().getName()).isEqualTo("이름");
        assertThat(findOne.getMember().getLoginId()).isEqualTo("아이디");
    }

    @Test
    @DisplayName("게시글 1개 조회 - 존재하지 않는 글")
    void test2_2() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        BoardCreate boardCreate = BoardCreate.builder()
                .title("제목")
                .content("내용")
                .build();

        Board board = boardRepository.save(Board.createBoard(boardCreate, member));

        // expected
        assertThatThrownBy(() -> boardService.findOne(board.getId() + 1))
                .isInstanceOf(ObjectNotFound.class);
    }

    //TODO 검색조건별 테스트 작성
    @Test
    @DisplayName("게시글 여러개 조회 - 5개씩 페이징")
    void test3() {
        //given
        for (int i = 0; i < 10; i++) {

            Member member = memberRepository.save(
                    Member.builder()
                            .name("이름" + i)
                            .loginId("아이디" + i)
                            .build()
            );

            BoardCreate boardCreate = BoardCreate.builder()
                    .title("제목" + i)
                    .content("내용" + i)
                    .build();

            boardRepository.save(Board.createBoard(boardCreate, member));
        }

        SearchOption search = new SearchOption();
        search.setSize(5);
        search.setPage(2);

        //when
        List<Board> list = boardService.findList(search);

        //then
        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(5);
        for (int i = 4; i >= 0; i--) {
            assertThat(list.get(4 - i).getTitle()).isEqualTo("제목" + i);
        }
    }

    @Test
    @DisplayName("게시글 여러개 조회 - 키워드 포함")
    void test3_2() {
        //given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        for (int i = 0; i < 3; i++) {

            BoardCreate boardCreate = BoardCreate.builder()
                    .title("제목" + i)
                    .content("내용" + i)
                    .build();

            boardRepository.save(Board.createBoard(boardCreate, member));
        }
        BoardCreate boardCreate = BoardCreate.builder()
                .title("제목조회용")
                .content("내용")
                .build();

        boardRepository.save(Board.createBoard(boardCreate, member));

        SearchOption search = new SearchOption();
        search.setSize(5);
        search.setPage(0);
        search.setKeyword("조회");

        //when
        List<Board> list = boardService.findList(search);

        //then
        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getTitle()).isEqualTo("제목조회용");
    }

    @Test
    @DisplayName("게시글 수정")
    void test4() {
        //given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        BoardCreate boardCreate = BoardCreate.builder()
                .title("제목")
                .content("내용")
                .build();

        Board board = boardRepository.save(Board.createBoard(boardCreate, member));

        BoardEdit boardEdit = BoardEdit.builder()
                .title("수정")
                .content("내용2")
                .build();
        //when
        boardService.edit(board.getId(),boardEdit,member);
        Board findBoard = boardRepository.findOne(board.getId()).get();

        //then
        assertThat(findBoard.getTitle()).isEqualTo("수정");
        assertThat(findBoard.getContent()).isEqualTo("내용2");

    }

    @Test
    @DisplayName("게시글 삭제")
    void test5() {
        //given
        Member member = memberRepository.save(
                Member.builder()
                        .name("이름")
                        .loginId("아이디")
                        .build()
        );

        BoardCreate boardCreate = BoardCreate.builder()
                .title("제목")
                .content("내용")
                .build();

        Board board = boardRepository.save(Board.createBoard(boardCreate, member));

        long count = boardRepository.count();
        //when
        boardService.delete(board.getId(),member);

        //then
        assertThat(boardRepository.count()).isEqualTo(count - 1);
        assertThatThrownBy(() -> boardService.delete(board.getId(),member))
                .isInstanceOf(ObjectNotFound.class);
        assertThatThrownBy(() -> boardService.findOne(board.getId()))
                .isInstanceOf(ObjectNotFound.class);
    }



}