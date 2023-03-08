package com.jjbin.mysite.api.service;

import com.jjbin.mysite.api.domain.Message;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.repository.MemberRepository;
import com.jjbin.mysite.api.repository.message.MessageRepository;
import com.jjbin.mysite.api.request.create.MessageCreate;
import com.jjbin.mysite.api.request.SearchOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    @Transactional
    public Long Write(MessageCreate messageCreate, Member from) {
        Member to = memberRepository.findByLoginId(messageCreate.getToLoginId()).orElseThrow(ObjectNotFound::new);
        Message message = Message.createMessage(from ,to,messageCreate.getContent());
        Message save = messageRepository.save(message);
        return save.getId();
    }


    @Transactional
    public void delete(Long id){
        Message message = messageRepository.findById(id)
                .orElseThrow(ObjectNotFound::new);

        messageRepository.delete(message);
    }
    public List<Message> findFromList(SearchOption searchOption, Long memberId){
        if(searchOption.getSize() == null){
            searchOption.setSize(10);
        }
        if(searchOption.getPage() == null){
            searchOption.setPage(0);
        }
        return messageRepository
                .findAllWithFrom(searchOption, memberId);
    }
    public List<Message> findToList(SearchOption searchOption, Long memberId){
        if(searchOption.getSize() == null){
            searchOption.setSize(10);
        }
        if(searchOption.getPage() == null){
            searchOption.setPage(0);
        }
        return messageRepository
                .findAllWithTo(searchOption, memberId);
    }

    public Message findOne(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(ObjectNotFound::new);

    }



}
