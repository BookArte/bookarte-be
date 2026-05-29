package com.library.bookarte.board.entity.type;

import com.library.bookarte.board.entity.*;
import lombok.Getter;

@Getter
public enum BoardType {
    NOTICE(Constants.NOTICE, Notice.class),
    NEWS(Constants.NEWS, News.class),
    FAQ(Constants.FAQ, Faq.class),
    QNA(Constants.QNA, Qna.class);

    private final String value;
    private final Class<? extends Board> entityClass;

    BoardType(String value, Class<? extends Board> entityClass) {
        this.value = value;
        this.entityClass = entityClass;
    }

    public static class Constants {
        public static final String NOTICE = "NOTICE";
        public static final String NEWS = "NEWS";
        public static final String FAQ = "FAQ";
        public static final String QNA = "QNA";
    }
}
