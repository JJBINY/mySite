package com.jjbin.mysite.api.repository.board;

import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long>, CommentRepositoryCustom {

    @Query("select c from Comment c join fetch c.member mb where c.id =:id")
    Optional<Comment> findOne(@Param("id") Long id);

    @Query("select count(c.id) from Comment c where c.board.id=:boardId")
    Long countComment(Long boardId);

    @Query("select count(c.id) from Comment c where c.board.id=:boardId and c.parent.id=:parentId")
    Long countReComment(Long boardId, Long parentId);
}
