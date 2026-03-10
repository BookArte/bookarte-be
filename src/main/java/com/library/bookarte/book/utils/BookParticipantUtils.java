package com.library.bookarte.book.utils;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.entity.type.ParticipantType;

import java.util.List;
import java.util.stream.Collectors;

public class BookParticipantUtils {
    public static String extractAuthors(List<Book.Participant> participants){
        return participants.stream()
                .filter(p -> p.getType() == ParticipantType.AUTHOR)
                .map(Book.Participant::getName)
                .collect(Collectors.joining(", "));
    }

    public static String extractTranslators(List<Book.Participant> participants) {
        return participants.stream()
                .filter(p -> p.getType() == ParticipantType.TRANSLATOR)
                .map(Book.Participant::getName)
                .collect(Collectors.joining(", "));
    }
}
