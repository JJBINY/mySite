package com.jjbin.mysite.api.repository;

import com.jjbin.mysite.api.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
}
