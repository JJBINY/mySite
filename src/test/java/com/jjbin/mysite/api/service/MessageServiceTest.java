package com.jjbin.mysite.api.service;

import com.jjbin.mysite.api.domain.Message;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.repository.message.MessageRepository;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.request.create.MessageCreate;
import com.jjbin.mysite.api.request.SearchOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MessageServiceTest {

    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MemberRepository memberRepository;



}