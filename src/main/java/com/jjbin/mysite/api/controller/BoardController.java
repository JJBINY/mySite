package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Comment;
import com.jjbin.mysite.api.domain.Like;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.exception.Unauthorized;
import com.jjbin.mysite.api.request.BoardEdit;
import com.jjbin.mysite.api.request.SearchOption;
import com.jjbin.mysite.api.request.create.BoardCreate;
import com.jjbin.mysite.api.request.create.CommentCreate;
import com.jjbin.mysite.api.response.BoardResponse;
import com.jjbin.mysite.api.response.CommentResponse;
import com.jjbin.mysite.api.response.LikeNum;
import com.jjbin.mysite.api.service.BoardService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.jjbin.mysite.api.SessionConst.LOGIN_MEMBER;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;


    @PostMapping("/board/write")
    public void write(@RequestBody @Valid BoardCreate boardCreate, HttpServletRequest request) {

        boardCreate.validate();
        Member member = (Member) request.getSession().getAttribute(LOGIN_MEMBER);
        Board board = Board.createBoard(boardCreate, member);
        boardService.write(board);
    }

    @GetMapping("/board/watch/{boardId}")
    public BoardResponse getOne(@PathVariable Long boardId) {
        return new BoardResponse(boardService.findOne(boardId));
    }


    @GetMapping("/board/list")
    public List<BoardResponse> getList(@ModelAttribute SearchOption searchOption) {
        return boardService.findList(searchOption).stream()
                .map(BoardResponse::new)
                .collect(Collectors.toList());
    }


    @PatchMapping("/board/{boardId}")
    public void edit(@PathVariable Long boardId, @RequestBody @Valid BoardEdit boardEdit, HttpServletRequest request) {
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);

        boardService.edit(boardId, boardEdit, member);
    }

    @DeleteMapping("/board/{boardId}")
    public void delete(@PathVariable Long boardId, HttpServletRequest request) {
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);

        boardService.delete(boardId,member);
    }

    //==코멘트==//
    @PostMapping("board/{boardId}/comment")
    public CommentResponse comment(@PathVariable Long boardId, @RequestBody @Valid CommentCreate commentCreate, HttpServletRequest request) {
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        Board board = boardService.findOne(boardId);

        Comment parent = null;
        if (commentCreate.getParentId() != null) {
            parent = boardService.findComment(commentCreate.getParentId());
        }
        return new CommentResponse(boardService.comment(
                Comment.builder()
                        .commentCreate(commentCreate)
                        .board(board)
                        .member(member)
                        .parent(parent)
                        .build()
        ));
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        boardService.deleteComment(commentId,member);
    }

    @GetMapping("/board/{boardId}/comments")
    public List<CommentResponse> getComments(@PathVariable Long boardId, @ModelAttribute SearchOption searchOption) {
        return boardService.findCommentList(boardId, searchOption).stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }
    @GetMapping("/comment/{commentId}/children")
    public List<CommentResponse> getChildComments(@PathVariable Long commentId, @ModelAttribute SearchOption searchOption) {
        return boardService.findChildList(commentId, searchOption).stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }

    //==좋아요==//
    @PostMapping("/board/{boardId}/like")
    public LikeNum likeBoard(@PathVariable Long boardId, HttpServletRequest request) {
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);

        return LikeNum.builder()
                .count(boardService.like(boardId, member))
                .build();
    }
}
