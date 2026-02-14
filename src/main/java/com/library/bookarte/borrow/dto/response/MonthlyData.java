package com.library.bookarte.borrow.dto.response;

public record MonthlyData(
        int year,
        int month,
        long count
){
    public String getLabel(){
        return String.format("%d-%02d", year, month);
    }
}
