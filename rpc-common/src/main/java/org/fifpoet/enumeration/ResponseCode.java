package org.fifpoet.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(1000, "调用成功"),
    FAIL(5000, "调用失败"),
    NOT_FOUND_METHOD(5001,"未找到指定方法"),
    NOT_FOUND_CLASS(5002,"未找到指定类");


    private final Integer code;
    private final String message;
}
