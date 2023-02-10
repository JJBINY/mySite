package com.jjbin.mysite.api.service.board;

import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Comment;
import com.jjbin.mysite.api.domain.Like;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.exception.Unauthorized;
import com.jjbin.mysite.api.repository.board.BoardRepository;
import com.jjbin.mysite.api.repository.board.CommentRepository;
import com.jjbin.mysite.api.repository.board.LikeRepository;
import com.jjbin.mysite.api.request.BoardEdit;
import com.jjbin.mysite.api.request.SearchOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;



    @Transactional
    public Comment comment(Comment comment) {
        Comment save = commentRepository.save(comment);
        return save;
    }

    public Comment findComment(Long commentId) {
        return commentRepository.findOne(commentId)
                .orElseThrow(ObjectNotFound::new);
    }

    @Transactional
    public void deleteComment(Long commentId, Member member) {

        Comment comment = commentRepository.findOne(commentId)
                .orElseThrow(ObjectNotFound::new);
        if (comment.getMember().getId() != member.getId()) {
            throw new Unauthorized();
        }
        commentRepository.delete(comment);
    }

    public List<Comment> findCommentList(Long boardId, SearchOption searchOption) {
        searchOption.validate();
        return commentRepository
                .findAllWithBoard(boardId, searchOption);
    }
    public List<Comment> findChildList(Long commentId, SearchOption searchOption) {
        searchOption.validate();
        return commentRepository
                .findChildren(commentId, searchOption);
    }

}
