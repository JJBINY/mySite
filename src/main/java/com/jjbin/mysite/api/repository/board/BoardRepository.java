package com.jjbin.mysite.api.repository.board;


import com.jjbin.mysite.api.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    @Query("select b from Board b join fetch b.member mb where b.id =:id")
    Optional<Board> findOne(@Param("id") Long id);

}
