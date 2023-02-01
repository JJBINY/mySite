package com.jjbin.mysite.api.repository.board;

import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.request.SearchOption;

import java.util.List;
import java.util.Optional;

public interface BoardRepositoryCustom {

    List<Board> findAll(SearchOption searchOption);
    List<Board> findAllWithMember(SearchOption searchOption, Long memberId);
    Optional<Board> findOneWithMember(Long boardId, Long memberId);
}
