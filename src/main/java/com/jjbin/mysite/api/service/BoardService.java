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
    public Long write(BoardCreate boardCreate, Member member) {
        Board board = Board.createBoard(boardCreate, member);
        Board save = boardRepository.save(board);
        //TODO send
        return save.getId();
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
    public void edit(Long id, BoardEdit boardEdit, Member member){
        Board board = boardRepository.findOne(id)
                .orElseThrow(ObjectNotFound::new);
        if (board.getMember().getId() != member.getId()) {
            throw new Unauthorized();
        }
        board.edit(boardEdit);
    }


    @Transactional
    public void delete(Long id, Member member){
        Board board = boardRepository.findOne(id)
                .orElseThrow(ObjectNotFound::new);
        if (board.getMember().getId() != member.getId()) {
            throw new Unauthorized();
        }
        boardRepository.delete(board);
    }




}
