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
public class LikeService {

    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public Long like(Long boardId, Member member) {

        Board board = boardRepository.findOne(boardId)
                .orElseThrow(ObjectNotFound::new);

        Like like = likeRepository.findByBoardIdAndMemberId(boardId, member.getId())
                .orElse(null);

        if(like!=null) {
            likeRepository.delete(like);
        }else {
            likeRepository.save(
                    Like.builder()
                            .board(board)
                            .member(member)
                            .build()
            );
        }
        return likeRepository.countLike(boardId);
    }

    public Long count(Long boardId) {
        return likeRepository.countLike(boardId);
    }
}
