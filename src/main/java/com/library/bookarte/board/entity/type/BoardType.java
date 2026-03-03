package com.library.bookarte.board.entity.type;

public enum BoardType {
    NOTICE(Constants.NOTICE),
    NEWS(Constants.NEWS);

    private final String value;

    BoardType(String value) {
        this.value = value;
    }

    public static class Constants {
        public static final String NOTICE = "NOTICE";
        public static final String NEWS = "NEWS";
    }
}
