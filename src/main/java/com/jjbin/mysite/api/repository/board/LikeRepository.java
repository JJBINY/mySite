package com.jjbin.mysite.api.repository.board;

import com.jjbin.mysite.api.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> , LikeRepositoryCustom{

    Optional<Like> findByBoardIdAndMemberId(Long boardId, Long memberId);

    @Query("select count(l.id) from Like l where l.board.id=:boardId")
    Long countLike(Long boardId);
}