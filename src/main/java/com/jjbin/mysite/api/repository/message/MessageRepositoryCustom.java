package com.jjbin.mysite.api.repository.message;

import com.jjbin.mysite.api.domain.Message;
import com.jjbin.mysite.api.request.SearchOption;

import java.util.List;
import java.util.Optional;

public interface MessageRepositoryCustom {

    List<Message> findAllWithFrom(SearchOption searchOption, Long from);
    List<Message> findAllWithTo(SearchOption searchOption, Long to);
}
