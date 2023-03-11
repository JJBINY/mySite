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
    public Map<String,Long> postLikeBoard(@PathVariable Long boardId, HttpServletRequest request) {
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        Long count = likeService.like(boardId, member);
        return Map.of("count", count);
    }

    //TODO 그냥 보드 불러올때 like갯수 같이 불러오는게 낫지 않을까?
    @GetMapping("/board/{boardId}/like")
    public Map<String,Long> getLikeBoard(@PathVariable Long boardId) {

        Long count = likeService.count(boardId);
        return Map.of("count", count);
    }
}
