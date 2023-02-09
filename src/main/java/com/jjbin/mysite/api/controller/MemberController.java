package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.SessionConst;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.InvalidRequest;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.request.LoginForm;
import com.jjbin.mysite.api.request.create.MemberCreate;
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
    }


}
