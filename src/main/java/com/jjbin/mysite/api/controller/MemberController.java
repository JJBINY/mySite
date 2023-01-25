package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.SessionConst;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.InvalidRequest;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.request.LoginForm;
import com.jjbin.mysite.api.request.MemberRequest;
import com.jjbin.mysite.api.response.MailResponse;
import com.jjbin.mysite.api.response.MemberResponse;
import com.jjbin.mysite.api.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.ObjectStreamException;
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
    private MemberResponse joinMember(@RequestBody @Valid MemberRequest request){
        log.info("add = {}",request.getAddress());
        log.info("req = {}",request);
        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(request.getPassword())
                .name(request.getName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        memberService.join(member);
        return new MemberResponse(member);
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public void login(@RequestBody @Valid LoginForm form,
                      BindingResult bindingResult,
                      HttpServletRequest request,
                      HttpServletResponse response){

        if (bindingResult.hasErrors()){
            throw new InvalidRequest(bindingResult.getObjectName(), "에러발생");
        }

        Member loginMember = memberService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}",loginMember);

        if (loginMember == null){
            log.info("로그인 실패");
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            response.setStatus(404);
            return;
        }
        log.info("로그인 성공");
        //로그인 성공 처리
        //세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();

        session.setAttribute(SessionConst.LOGIN_MEMBER,loginMember);
        response.setStatus(200);
        return;
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();//세션제거
        }
        return;
    }

    /**
     * 세션으로 멤버 조회
     */
    @GetMapping("/member")
    public MemberResponse findSessionMember(HttpServletRequest request){
        log.info("세션조회");
        HttpSession session = request.getSession(false);
        if(session == null){
            throw new ObjectNotFound();
        }
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        return new MemberResponse(member);
    }


    /**
     * 멤버 조회 : id로 단건 조회
     */
    @GetMapping("/member/{memberId}")
    public MemberResponse findMember(@PathVariable Long memberId){
        Member member = memberService.findOne(memberId).orElseThrow(IllegalArgumentException::new);
        return new MemberResponse(member);
    }

    /**
     * 전체 멤버 조회
     */
    @GetMapping("/members")
    public List<MemberResponse> findMembers(){
        return memberService.findAll().stream()
                .map(MemberResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 계층구조 필요
     * member{
     * mail}
     * }
     * }
     */
    @GetMapping("/member/{memberId}/mail/list")
    public List<MailResponse> memberMails(@PathVariable Long memberId) {
        return memberService.findMails(memberId).stream()
                .map(MailResponse::new)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/member/{memberId}/mail/{mailId}")
    public void findMemberMail(){

    }
}
