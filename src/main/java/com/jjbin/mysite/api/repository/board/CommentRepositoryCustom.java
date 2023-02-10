package com.jjbin.mysite.api.repository.board;

import com.jjbin.mysite.api.domain.Comment;
import com.jjbin.mysite.api.request.SearchOption;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> findAllWithBoard(Long boardId, SearchOption searchOption);

    List<Comment> findAllWithMember(Long memberId, SearchOption searchOption);


    List<Comment> findChildren(Long parentId, SearchOption searchOption);
}
