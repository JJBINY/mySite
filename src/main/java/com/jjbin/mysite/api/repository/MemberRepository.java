package com.jjbin.mysite.api.repository;

import com.jjbin.mysite.api.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    @Query("select mb from Member mb where mb.loginId = :loginId")
    Optional<Member> findByLoginId(@Param("loginId") String loginId);

}
