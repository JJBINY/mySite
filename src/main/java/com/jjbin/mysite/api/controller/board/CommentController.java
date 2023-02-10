package com.jjbin.mysite.api.controller.board;

import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Comment;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.request.SearchOption;
import com.jjbin.mysite.api.request.create.CommentCreate;
import com.jjbin.mysite.api.response.CommentResponse;
import com.jjbin.mysite.api.service.board.BoardService;
import com.jjbin.mysite.api.service.board.CommentService;
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
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;

    //==코멘트==//
    @PostMapping("board/{boardId}/comment")
    public CommentResponse comment(@PathVariable Long boardId, @RequestBody @Valid CommentCreate commentCreate, HttpServletRequest request) {
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        Board board = boardService.findOne(boardId);

        Comment parent = null;
        if (commentCreate.getParentId() != null) {
            parent = commentService.findComment(commentCreate.getParentId());
        }
        return new CommentResponse(commentService.comment(
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
        commentService.deleteComment(commentId,member);
    }

    @GetMapping("/board/{boardId}/comments")
    public List<CommentResponse> getComments(@PathVariable Long boardId, @ModelAttribute SearchOption searchOption) {
        return commentService.findCommentList(boardId, searchOption).stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }
    @GetMapping("/comment/{commentId}/children")
    public List<CommentResponse> getChildComments(@PathVariable Long commentId, @ModelAttribute SearchOption searchOption) {
        return commentService.findChildList(commentId, searchOption).stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }

}
