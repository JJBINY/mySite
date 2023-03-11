package com.jjbin.mysite.api.repository.message;


import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryCustom {


    @Query("select m from Message m join fetch m.from join fetch m.to where m.id =:id")
    Optional<Message> findOne(@Param("id") Long id);

    @Query("select count(m.id) from Message m where m.to.id=:id")
    int countRcvMessage(@Param("id") Long id);
}
