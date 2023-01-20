package com.jjbin.mysite.api.service;


import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member) {
        Member save = memberRepository.save(member);
        return save.getId();
    }

    @Transactional
    public void withdraw(Long id){
        memberRepository.deleteById(id);
    }

    public List<Member> findAll(){
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(Long id){
        return memberRepository.findById(id);
    }
}
