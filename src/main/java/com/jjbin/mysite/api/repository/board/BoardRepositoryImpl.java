package com.jjbin.mysite.api.repository.board;

import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Like;
import com.jjbin.mysite.api.request.SearchOption;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {


    private final EntityManager em;


    @Override
    public List<Board> findAll(SearchOption searchOption) {

        if(searchOption.getKeyword() !=null){
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
    public List<Board> findAllWithMember(Long memberId, SearchOption searchOption) {

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

//    @Override
//    public Like save(Like like) {
//        String sql = "insert into likes (board_id, member_id) " +
//                " values (:boardId, :memberId)";
//
//
//    }
}
