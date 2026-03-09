package com.library.bookarte.board.entity.type;

import com.library.bookarte.board.entity.Board;
import com.library.bookarte.board.entity.News;
import com.library.bookarte.board.entity.Notice;
import lombok.Getter;

@Getter
public enum BoardType {
    NOTICE(Constants.NOTICE, Notice.class),
    NEWS(Constants.NEWS, News.class);

    private final String value;
    private final Class<? extends Board> entityClass;

    BoardType(String value, Class<? extends Board> entityClass) {
        this.value = value;
        this.entityClass = entityClass;
    }

    public static class Constants {
        public static final String NOTICE = "NOTICE";
        public static final String NEWS = "NEWS";
    }
}
