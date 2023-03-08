package com.jjbin.mysite.api.repository.message;


import com.jjbin.mysite.api.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryCustom {

}
