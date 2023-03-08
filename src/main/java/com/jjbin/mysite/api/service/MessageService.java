package com.jjbin.mysite.api.service;

import com.jjbin.mysite.api.domain.Message;
import com.jjbin.mysite.api.domain.Member;
import com.jjbin.mysite.api.exception.ObjectNotFound;
import com.jjbin.mysite.api.repository.message.MessageRepository;
import com.jjbin.mysite.api.request.create.MessageCreate;
import com.jjbin.mysite.api.request.SearchOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MailService {

    private final MessageRepository messageRepository;

    @Transactional
    public Long write(MessageCreate messageCreate, Member member) {
        Message message = Message.createMessage(messageCreate, member);
        Message save = messageRepository.save(message);
        //TODO send
        return save.getId();
    }

    /**
     * TODO smtp 서버 구축 필요
     *
     */
    public void send(Message mail){
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
//        sender.setHost("smtp.gmail.com");
//        sender.setPort(587);
//        sender.setUsername("sisoya0424@gamil.com");
//        sender.setPassword("xbsnanlsjouibbgw");
//        Properties props = sender.getJavaMailProperties();
//        props.put("mail.smtp.auth", true);
//        props.put("mail.smtp.starttls.enable", true);

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        try {
            messageHelper.setFrom(new InternetAddress(mail.getMember().getLoginId(),"sender","utf-8"));
            messageHelper.setTo(mail.getDestination());
            messageHelper.setSubject(mail.getTitle());
            messageHelper.setText(mail.getContent());
            messageHelper.setSentDate(Date.from(mail.getCreatedAt().toInstant(ZoneOffset.ofHours(9))));
        } catch (Exception e) {
            log.info("전송실패");
            throw new RuntimeException(e);
        }
        log.info("전송시도");

        sender.send(message);
        log.info("전송성공");

    }

    @Transactional
    public void delete(Long id){
        Message message = messageRepository.findById(id)
                .orElseThrow(ObjectNotFound::new);

        messageRepository.delete(message);
    }

    // TODO 검색옵션
    public List<Message> findList(SearchOption searchOption, Long memberId){
        if(searchOption.getSize() == null){
            searchOption.setSize(10);
        }
        if(searchOption.getPage() == null){
            searchOption.setPage(0);
        }
        return messageRepository
                .findAllWithMember(searchOption, memberId);
    }

    public Message findOne(Long id, Long memberId) {

        return messageRepository.findOneWithMember(id,memberId)
                .orElseThrow(ObjectNotFound::new);

    }



}
