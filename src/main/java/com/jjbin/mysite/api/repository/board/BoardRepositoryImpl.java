package com.jjbin.mysite.api.repository.board;

import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.request.SearchOption;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {


    private final EntityManager em;


    @Override
    public List<Board> findAll(SearchOption searchOption) {

        if(!searchOption.getKeyword().isBlank()){
            return em.createQuery("select b from Board b" +
                            " join fetch b.member mb" +
                            " where b.title like concat('%',:keyword,'%')" +
                            " order by b.id desc", Board.class)
                    .setParameter("keyword",searchOption.getKeyword())
                    .setFirstResult(searchOption.getOffset())
                    .setMaxResults(searchOption.getSize())
                    .getResultList();
        }
        return em.createQuery("select b from Board b" +
                        " join fetch b.member mb" +
                        " order by b.id desc", Board.class)
                .setFirstResult(searchOption.getOffset())
                .setMaxResults(searchOption.getSize())
                .getResultList();

    }
    @Override
    public List<Board> findAllWithMember(SearchOption searchOption, Long memberId) {

        return em.createQuery(
                "select b from Board b" +
                        " join fetch b.member mb" +
                        " where mb.id=:id" +
                        " order by b.id desc", Board.class)
                .setParameter("id",memberId)
                .setFirstResult(searchOption.getOffset())
                .setMaxResults(searchOption.getSize())
                .getResultList();

    }
    @Override
    public Optional<Board> findOneWithMember(Long boardId ,Long memberId) {
        return em.createQuery(
                        "select b from Board b" +
                                " join fetch b.member mb" +
                                " where b.id=:boardId and mb.id=:memberId", Board.class)
                .setParameter("boardId", boardId)
                .setParameter("memberId", memberId)
                .getResultList().stream().findAny();
    }
}
