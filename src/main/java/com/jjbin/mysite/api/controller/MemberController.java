package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.repository.MemberDto;
import com.jjbin.mysite.api.request.MemberRequest;
import com.jjbin.mysite.api.response.MemberResponse;
import com.jjbin.mysite.api.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("member/join")
    private MemberResponse joinMember(@RequestBody @Valid MemberRequest request){
        Member member = Member.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        memberService.join(member);
        return new MemberResponse(member);
    }

    @GetMapping("/member/{memberId}")
    public MemberResponse findMember(@PathVariable Long memberId){
        Member member = memberService.findOne(memberId).orElseThrow(IllegalArgumentException::new);
        return new MemberResponse(member);
    }

    @GetMapping("/members")
    public List<MemberDto> findMembers(){
        return memberService.findAll().stream()
                .map(MemberDto::new)
                .collect(Collectors.toList());
    }

}
