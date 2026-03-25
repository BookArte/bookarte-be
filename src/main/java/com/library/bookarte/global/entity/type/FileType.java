package com.library.bookarte.global.entity.type;

import lombok.Getter;

@Getter
public enum FileType {
    THUMBNAIL(FileType.Constants.THUMBNAIL),
    FILE(FileType.Constants.FILE);

    private final String value;

    FileType(String value) {
        this.value = value;
    }

    public static class Constants {
        public static final String THUMBNAIL = "THUM";
        public static final String FILE = "FILE";
    }
}
