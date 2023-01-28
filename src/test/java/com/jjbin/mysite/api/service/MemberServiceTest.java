package com.jjbin.mysite.api.service;

import com.jjbin.mysite.api.domain.Address;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.Conflicted;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.request.MemberCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 가입")
    void test1() {
        //given
        MemberCreate memberCreate = MemberCreate.builder()
                .loginId("loginId")
                .password("password")
                .name("name")
                .phone("010-1234-1234")
                .address(new Address("seoul", "gwangjin", "123"))
                .build();
        long count = memberRepository.count();

        //when
        Long memberId = memberService.join(memberCreate);
        Member member = memberRepository.findById(memberId).get();
        //then
        assertThat(memberRepository.count()).isEqualTo(count + 1);
        assertThat(member.getLoginId()).isEqualTo("loginId");
        assertThat(member.getPassword()).isEqualTo("password");
        assertThat(member.getName()).isEqualTo("name");
        assertThat(member.getPhone()).isEqualTo("010-1234-1234");
        assertThat(member.getAddress().getCity()).isEqualTo("seoul");
        assertThat(member.getAddress().getStreet()).isEqualTo("gwangjin");
        assertThat(member.getAddress().getZipcode()).isEqualTo("123");
    }

    @Test
    @DisplayName("회원 가입 - 이미 존재하는 회원(아이디)")
    void test1_1() {
        //given
        MemberCreate memberCreate = MemberCreate.builder()
                .loginId("loginId")
                .password("password")
                .name("name")
                .phone("010-1234-1234")
                .address(new Address("seoul", "gwangjin", "123"))
                .build();
        long count = memberRepository.count();
        memberService.join(memberCreate);

        //expected
        assertThatThrownBy(() -> memberService.join(memberCreate))
                .isInstanceOf(Conflicted.class)
                .hasMessage("이미 존재하는 회원 아이디");
    }

    @Test
    @DisplayName("회원 로그인")
    void test2() {
        //given
        MemberCreate memberCreate = MemberCreate.builder()
                .loginId("loginId")
                .password("1234")
                .name("name")
                .phone("010-1234-1234")
                .address(new Address("seoul", "gwangjin", "123"))
                .build();

        memberService.join(memberCreate);

        //when
        Member loginMember = memberService.login("loginId", "1234");

        //then
        assertThat(loginMember).isNotNull();
        assertThat(loginMember.getLoginId()).isEqualTo("loginId");
        assertThat(loginMember.getPassword()).isEqualTo("1234");
    }
    @Test
    @DisplayName("회원 로그인 - 잘못된 아이디 요청")
    void test2_1() {
        //given
        MemberCreate memberCreate = MemberCreate.builder()
                .loginId("loginId")
                .password("1234")
                .name("name")
                .phone("010-1234-1234")
                .address(new Address("seoul", "gwangjin", "123"))
                .build();

        memberService.join(memberCreate);

        //expected
        assertThatThrownBy(() -> memberService.login("wrongLoginId", "1234"))
                .isInstanceOf(ObjectNotFound.class)
                .hasMessage("아이디 또는 비밀번호가 잘못되었습니다.");
    }

    @Test
    @DisplayName("회원 로그인 - 잘못된 비밀번호 요청")
    void test2_2() {
        //given
        MemberCreate memberCreate = MemberCreate.builder()
                .loginId("loginId")
                .password("1234")
                .name("name")
                .phone("010-1234-1234")
                .address(new Address("seoul", "gwangjin", "123"))
                .build();

        memberService.join(memberCreate);

        //expected
        assertThatThrownBy(() -> memberService.login("loginId", "wrongPassword"))
                .isInstanceOf(ObjectNotFound.class)
                .hasMessage("아이디 또는 비밀번호가 잘못되었습니다.");
    }

    @Test
    @DisplayName("id로 회원 조회")
    void test3() {
        //given
        MemberCreate memberCreate = MemberCreate.builder()
                .loginId("loginId")
                .password("password")
                .name("name")
                .phone("010-1234-1234")
                .address(new Address("seoul", "gwangjin", "123"))
                .build();

        Long memberId = memberService.join(memberCreate);

        //when
        Member member = memberService.findOne(memberId);

        //then
        assertThat(member.getLoginId()).isEqualTo("loginId");
        assertThat(member.getPassword()).isEqualTo("password");
        assertThat(member.getName()).isEqualTo("name");
        assertThat(member.getPhone()).isEqualTo("010-1234-1234");
        assertThat(member.getAddress().getCity()).isEqualTo("seoul");
        assertThat(member.getAddress().getStreet()).isEqualTo("gwangjin");
        assertThat(member.getAddress().getZipcode()).isEqualTo("123");
    }

    @Test
    @DisplayName("id로 회원 조회 - 존재하지 않는 회원")
    void test3_1() {
        //given
        MemberCreate memberCreate = MemberCreate.builder()
                .loginId("loginId")
                .password("password")
                .name("name")
                .phone("010-1234-1234")
                .address(new Address("seoul", "gwangjin", "123"))
                .build();

        Long memberId = memberService.join(memberCreate);
        memberRepository.deleteById(memberId);

        //expected
        assertThatThrownBy(() -> memberService.findOne(memberId))
                .isInstanceOf(ObjectNotFound.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

}