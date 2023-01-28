package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.SessionConst;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.InvalidRequest;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.request.LoginForm;
import com.jjbin.mysite.api.response.MemberResponse;
import com.jjbin.mysite.api.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final MemberService memberService;

//    /**
//     * TODO 임시용; 고정로그인 기능 개선필요
//     * 세션으로 멤버 조회
//     */
//    @GetMapping("/member")
//    public MemberResponse findSessionMember(HttpServletRequest request){
//        log.info("세션조회");
//        HttpSession session = request.getSession(false);
//        if(session == null){
//            throw new ObjectNotFound();
//        }
//        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
//        return new MemberResponse(member);
//    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public void login(@RequestBody @Valid LoginForm form,
                      BindingResult bindingResult,
                      HttpServletRequest request,
                      HttpServletResponse response){

        if (bindingResult.hasErrors()){
            throw new InvalidRequest(bindingResult.getObjectName(), "유효하지 않은 값");
        }

        Member loginMember = memberService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}",loginMember);

        if (loginMember == null){
            log.info("로그인 실패");
            throw new ObjectNotFound("아이디 또는 비밀번호가 맞지 않습니다.");
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

}
