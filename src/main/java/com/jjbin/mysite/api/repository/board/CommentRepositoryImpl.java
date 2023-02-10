package com.jjbin.mysite.api.repository.board;

import com.jjbin.mysite.api.domain.Comment;
import com.jjbin.mysite.api.request.SearchOption;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {


    private final EntityManager em;


    @Override
    public List<Comment> findAllWithBoard(Long boardId, SearchOption searchOption) {

        return em.createQuery("select c from Comment c" +
                        " join fetch c.member mb" +
                        " where c.board.id=:boardId" +
                        " order by c.id desc", Comment.class)
                .setParameter("boardId", boardId)
                .setFirstResult(searchOption.getOffset())
                .setMaxResults(searchOption.getSize())
                .getResultList();

    }
    @Override
    public List<Comment> findAllWithMember(Long memberId, SearchOption searchOption) {

        return em.createQuery(
                "select c from Comment c" +
                        " join fetch c.member mb" +
                        " where mb.id=:id" +
                        " order by c.id desc", Comment.class)
                .setParameter("id",memberId)
                .setFirstResult(searchOption.getOffset())
                .setMaxResults(searchOption.getSize())
                .getResultList();


    }
    @Override
    public List<Comment> findChildren(Long parentId, SearchOption searchOption) {

        return em.createQuery("select c from Comment c" +
                        " where c.parent.id=:parentId" +
                        " order by c.id desc", Comment.class)
                .setParameter("parentId", parentId)
                .setFirstResult(searchOption.getOffset())
                .setMaxResults(searchOption.getSize())
                .getResultList();

    }
}
