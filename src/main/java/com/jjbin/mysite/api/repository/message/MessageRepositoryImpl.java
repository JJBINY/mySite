package com.jjbin.mysite.api.repository.message;

import com.jjbin.mysite.api.domain.Message;
import com.jjbin.mysite.api.request.SearchOption;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepositoryCustom {


    private final EntityManager em;


    @Override
    public List<Message> findAllWithFrom(SearchOption searchOption, Long from) {

        return em.createQuery(
                "select m from Message m" +
                        " join fetch m.from f" +
                        " where f.id=:id" +
                        " order by m.id desc", Message.class)
                .setParameter("id",from)
                .setFirstResult(searchOption.getOffset())
                .setMaxResults(searchOption.getSize())
                .getResultList();

    }

}
