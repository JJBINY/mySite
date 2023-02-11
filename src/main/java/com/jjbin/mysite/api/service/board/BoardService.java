package com.jjbin.mysite.api.service.board;

import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Comment;
import com.jjbin.mysite.api.domain.Like;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.exception.Unauthorized;
import com.jjbin.mysite.api.repository.board.LikeRepository;
import com.jjbin.mysite.api.repository.board.CommentRepository;
import com.jjbin.mysite.api.repository.board.BoardRepository;
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
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public Long write(Board board) {
        return boardRepository.save(board).getId();
    }

    public Board findOne(Long id) {

        Board board = boardRepository.findOne(id)
                .orElseThrow(ObjectNotFound::new);

        return board;
    }


    // 작상자별 글 모아보기
    public List<Board> findList(SearchOption searchOption){
        if(searchOption.getSize() == null){
            searchOption.setSize(10);
        }
        if(searchOption.getPage() == null){
            searchOption.setPage(0);
        }
        return boardRepository
                .findAll(searchOption);
    }

    @Transactional
    public void edit(Long boardId, BoardEdit boardEdit, Member member){

        Board board = boardRepository.findOne(boardId)
                .orElseThrow(ObjectNotFound::new);

        if (board.getMember().getId() != member.getId()) {
            throw new Unauthorized();
        }

        board.edit(boardEdit);

    }


    @Transactional
    public void delete(Long boardId, Member member){
        Board board = boardRepository.findOne(boardId)
                .orElseThrow(ObjectNotFound::new);

        if (board.getMember().getId() != member.getId()) {
            throw new Unauthorized();
        }
        boardRepository.delete(board);
    }

    public Long count() {
        return boardRepository.countBoard();
    }
}
