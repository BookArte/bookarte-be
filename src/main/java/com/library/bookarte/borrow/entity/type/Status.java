package com.library.bookarte.borrow.entity.type;

import lombok.Getter;

@Getter
public enum Status {
    /*대출 중*/
    BORROWED,

    /*반납 신청*/
    RETURN_REQUESTED,

    /*반납 완료*/
    RETURNED,

    /*연체 중*/
    OVERDUE;
}
