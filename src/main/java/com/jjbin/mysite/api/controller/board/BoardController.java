package com.jjbin.mysite.api.controller.board;

import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.request.BoardEdit;
import com.jjbin.mysite.api.request.SearchOption;
import com.jjbin.mysite.api.request.create.BoardCreate;
import com.jjbin.mysite.api.response.BoardResponse;
import com.jjbin.mysite.api.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/board/count")
    public Map<String,Long> getCount(){
        return Map.of("count", boardService.count());
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


}
