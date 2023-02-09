package com.jjbin.mysite.api.repository.mail;

import com.jjbin.mysite.api.domain.Mail;
import com.jjbin.mysite.api.request.SearchOption;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class MailRepositoryImpl implements MailRepositoryCustom {


    private final EntityManager em;


    @Override
    public List<Mail> findAllWithMember(SearchOption searchOption, Long memberId) {

        return em.createQuery(
                "select m from Mail m" +
                        " join fetch m.member mb" +
                        " where mb.id=:id" +
                        " order by m.id desc",Mail.class)
                .setParameter("id",memberId)
                .setFirstResult(searchOption.getOffset())
                .setMaxResults(searchOption.getSize())
                .getResultList();

    }
    @Override
    public Optional<Mail> findOneWithMember(Long mailId ,Long memberId) {
        return em.createQuery(
                        "select m from Mail m" +
                                " join fetch m.member mb" +
                                " where m.id=:mailId and mb.id=:memberId", Mail.class)
                .setParameter("mailId", mailId)
                .setParameter("memberId", memberId)
                .getResultList().stream().findAny();
    }
}
