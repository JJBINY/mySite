package com.jjbin.mysite.api.service;


import com.jjbin.mysite.api.crypto.PasswordEncoder;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.Conflicted;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.request.create.MemberCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Long join(MemberCreate memberCreate) {
        if(memberRepository.findByLoginId(memberCreate.getLoginId()).isPresent()){
            throw new Conflicted("이미 존재하는 회원 아이디");
        }

        PasswordEncoder encoder = new PasswordEncoder();
        String encodedPw= encoder.encode(memberCreate.getPassword());

        if(!encoder.matches(memberCreate.getPassword(), encodedPw)){
            throw new RuntimeException();//
        };
        memberCreate.setPassword(encodedPw);
        Member member = Member.createMember(memberCreate);
        Member save = memberRepository.save(member);
        return save.getId();
    }

    /**
     * TODO 회원탈퇴
     */
    @Transactional
    public void withdraw(Long id){
        memberRepository.deleteById(id);
    }

    /**
     * 로그인 - 아이디와 비밀번호가 저장된 데이터와 일치하는지 확인
     */
    public Member login(String loginId, String password){
        log.info("요청 로그인아이디 = {}", loginId);
//        Member member = memberRepository.findByLoginId(loginId)
//                .filter(m -> m.getPassword().equals(password))
//                .orElse(null);
        PasswordEncoder encoder = new PasswordEncoder();
        Member member = memberRepository.findByLoginId(loginId)
                .filter(m -> encoder.matches(password,m.getPassword()))
                .orElse(null);
        if(member == null){
            throw new ObjectNotFound("아이디 또는 비밀번호가 잘못되었습니다.");
        }
        return member;

    }

    public Member findOne(Long id){
        return memberRepository.findById(id)
                .orElseThrow(()->new ObjectNotFound("존재하지 않는 회원입니다."));
    }

    public List<Member> findAll(){
        return memberRepository.findAll();
    }


}
