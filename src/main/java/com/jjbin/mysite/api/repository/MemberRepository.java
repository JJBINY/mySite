package com.jjbin.mysite.api.repository;

import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    // TODO N+1문제 확인하기
    @Query("select m from Mail m join m.member mb where m.id =:memberId")
    List<Mail> findMailsById(@Param("memberId") Long memberId);

    @Query("select mb from Member mb where mb.loginId = :loginId")
    Optional<Member> findByLoginId(@Param("loginId") String loginId);

}
