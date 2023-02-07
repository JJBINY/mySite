package com.jjbin.mysite.api.service;

import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.exception.Unauthorized;
import com.jjbin.mysite.api.repository.board.BoardRepository;
import com.jjbin.mysite.api.request.BoardEdit;
import com.jjbin.mysite.api.request.SearchOption;
import com.jjbin.mysite.api.request.create.BoardCreate;
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
    public void edit(Board board, BoardEdit boardEdit){

        board.edit(boardEdit);
        boardRepository.save(board); //controller단에서 board 가지고 와서 더티체킹 작동안함

    }


    @Transactional
    public void delete(Long boardId){
        Board board = boardRepository.findOne(boardId)
                .orElseThrow(ObjectNotFound::new);
        boardRepository.delete(board);
    }




}
