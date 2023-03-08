package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.request.create.MessageCreate;
import com.jjbin.mysite.api.request.SearchOption;
import com.jjbin.mysite.api.response.MessageResponse;
import com.jjbin.mysite.api.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.jjbin.mysite.api.SessionConst.LOGIN_MEMBER;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;


    @PostMapping("/message/create")
    public void sendMessage(@RequestBody @Valid MessageCreate messageCreate, HttpServletRequest request) {

        messageCreate.validate();
        Member member = (Member) request.getSession().getAttribute(LOGIN_MEMBER);

        messageService.Write(messageCreate,member);
    }


    @GetMapping("/message/fromList")
    public List<MessageResponse> messageFromList(@ModelAttribute SearchOption searchOption, HttpServletRequest request) {
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        return messageService.findFromList(searchOption,member.getId()).stream()
                .map(MessageResponse::new)
                .collect(Collectors.toList());
    }
    @GetMapping("/message/toList")
    public List<MessageResponse> messageToList(@ModelAttribute SearchOption searchOption, HttpServletRequest request) {
        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        return messageService.findToList(searchOption,member.getId()).stream()
                .map(MessageResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/message/{mailId}")
    public MessageResponse findMessage(@PathVariable Long mailId, HttpServletRequest request){
//        Member member = (Member) request.getSession(false).getAttribute(LOGIN_MEMBER);
        return new MessageResponse(messageService.findOne(mailId));
    }

    @DeleteMapping("/message/{mailId}")
    public void deleteMessage(@PathVariable Long mailId){
        messageService.delete(mailId);
    }
}
