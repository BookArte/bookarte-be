package com.library.bookarte.book.validator;

import com.library.bookarte.book.dto.request.BookReqDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BookThumbnailValidator implements ConstraintValidator<AtLeastOneThumbnail, BookReqDto> {
    @Override
    public boolean isValid(BookReqDto dto, ConstraintValidatorContext context) {
        boolean hasUrl = dto.getBookThumbnail() != null && !dto.getBookThumbnail().isBlank();
        boolean hasFile = dto.getBookThumbnailFile() != null && !dto.getBookThumbnailFile().isEmpty();

        boolean isValid = hasUrl || hasFile;

        if(!isValid){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("썸네일 URL 또는 파일 중 하나는 필수입니다.")
                    .addPropertyNode("bookThumbnail")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
