package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.request.BoardEdit;
import com.jjbin.mysite.api.request.SearchOption;
import com.jjbin.mysite.api.request.create.BoardCreate;
import com.jjbin.mysite.api.response.BoardResponse;
import com.jjbin.mysite.api.service.BoardService;
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

        boardService.write(boardCreate,member);
    }


    @GetMapping("/board/list")
    public List<BoardResponse> getList(@ModelAttribute SearchOption searchOption, HttpServletRequest request) {
//        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        return boardService.findList(searchOption).stream()
                .map(BoardResponse::new)
                .collect(Collectors.toList());
    }


    @GetMapping("/board/{boardId}")
    public BoardResponse getOne(@PathVariable Long boardId,HttpServletRequest request){
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        return new BoardResponse(boardService.findOne(boardId));
    }

    @PatchMapping("/board/{boardId}")
    public void edit(@PathVariable Long boardId, @RequestBody @Valid BoardEdit boardEdit, HttpServletRequest request){
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        boardService.edit(boardId,boardEdit,member);
    }

    @DeleteMapping("/board/{boardId}")
    public void delete(@PathVariable Long boardId, HttpServletRequest request){
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        boardService.delete(boardId, member);
    }
}
