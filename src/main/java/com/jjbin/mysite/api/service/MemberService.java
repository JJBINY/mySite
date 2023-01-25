package com.jjbin.mysite.api.service;


import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member) {
        Member save = memberRepository.save(member);
        return save.getId();
    }

    /**
     * 회원탈퇴
     */
    @Transactional
    public void withdraw(Long id){
        memberRepository.deleteById(id);
    }

    /**
     * @return null이면 로그인 실패
     */
    public Member login(String loginId, String password){
        Optional<Member> member = memberRepository.findByLoginId(loginId);
        log.info("요청 로그인아이디 = {}", loginId);
        return member
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);

    }

    public List<Member> findAll(){
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(Long id){
        return memberRepository.findById(id);
    }

    /**
     * 회원 메일 조회
     * 회원 id로 회원의 메일 조회
     */
    public List<Mail> findMails(Long id){
        return memberRepository.findMailsById(id);}


}
