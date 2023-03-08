package com.jjbin.mysite.api.response;

import com.jjbin.mysite.api.domain.Address;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.domain.Message;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.repository.message.MessageRepository;
import com.jjbin.mysite.api.request.SearchOption;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
public class MemberResponse {

    @Autowired
    private MessageRepository messageRepository;

    private Long id;
    private String loginId;
    private String name;

    private String phone;   //공백없는 문자열 핸드폰번호

    private Address address;

    private long newMsg;

    public MemberResponse(Member member) {
        this.loginId = member.getLoginId();
        this.id = member.getId();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.address = member.getAddress();
        this.newMsg = countNewMsg(member);
    }

    private long countNewMsg(Member member) {
        List<Message> messages = messageRepository.findAllWithFrom(new SearchOption(), member.getId());
        long count = messages.stream().filter(a->a.isRead()).count();
        return count;
    }
}
