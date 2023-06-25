package org.fifpoet.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(1000, "request success"),
    FAIL(5000, "request failed"),
    NOT_FOUND_METHOD(5001,"method not found"),
    NOT_FOUND_CLASS(5002,"class not found");


    private final Integer code;
    private final String message;
}
