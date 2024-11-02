package com.yudiind.OnlineShop_Electronic.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.message.Message;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ApiError {

    private HttpStatus status;
    private String message;
    private Integer error;


}
