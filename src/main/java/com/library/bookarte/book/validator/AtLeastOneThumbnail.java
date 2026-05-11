package com.library.bookarte.book.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookThumbnailValidator.class)
public @interface AtLeastOneThumbnail {
    String message() default "썸네일 URL 또는 파일 중 하나는 필수입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
