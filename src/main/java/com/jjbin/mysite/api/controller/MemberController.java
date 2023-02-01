package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.SessionConst;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.request.create.MemberCreate;
import com.jjbin.mysite.api.response.MemberResponse;
import com.jjbin.mysite.api.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;



    /**
     *  회원가입
     */
    @PostMapping("/join")
    private void join(@RequestBody @Valid MemberCreate request){
        request.validate();
        memberService.join(request);
    }
//
//    /**
//     *  TODO 회원정보수정
//     */
//    @PostMapping("/member/update")
//    private void update(@RequestBody ){}




    /**
     * 회원 조회 : id로 단건 조회
     */
    @GetMapping("/member")
    public MemberResponse findMember(HttpServletRequest request){
        Member member = (Member) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
        return new MemberResponse(memberService.findOne(member.getId()));
    }

    /**
     * 전체 회원 조회
     */
    @GetMapping("/members")
    public List<MemberResponse> findMembers(){
        return memberService.findAll().stream()
                .map(MemberResponse::new)
                .collect(Collectors.toList());
    }


}
