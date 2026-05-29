package com.library.bookarte.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

   private final CustomErrorCode customErrorCode;
   private final String message;

   public CustomException(CustomErrorCode customErrorCode){
       super(customErrorCode.getMessage());
       this.customErrorCode = customErrorCode;
       this.message = customErrorCode.getMessage();
   }

   public CustomException(CustomErrorCode customErrorCode, String deailMessage){
       super(deailMessage);
       this.customErrorCode = customErrorCode;
       this.message = deailMessage;
   }
}
