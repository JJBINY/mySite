package com.jjbin.mysite.api.controller.board;

import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.service.board.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

import static com.jjbin.mysite.api.SessionConst.LOGIN_MEMBER;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/board/{boardId}/like")
    public Map<String,Long> likeBoard(@PathVariable Long boardId, HttpServletRequest request) {
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        Long count = likeService.like(boardId, member);
        return Map.of("count", count);
    }
}
