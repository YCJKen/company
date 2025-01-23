package com.wustl.company.common;

public class Results {
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;
    public static final String SUCCESS_MESSAGE = "操作成功";
    public static final String ERROR_MESSAGE = "操作失败";

    public static <T> Result<T> success() {
        return Result.<T>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .build();
    }

    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .data(data)
                .build();
    }

    public static <T> Result<T> success(String message, T data) {
        return Result.<T>builder()
                .code(SUCCESS_CODE)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> Result<T> error() {
        return Result.<T>builder()
                .code(ERROR_CODE)
                .message(ERROR_MESSAGE)
                .build();
    }

    public static <T> Result<T> error(String message) {
        return Result.<T>builder()
                .code(ERROR_CODE)
                .message(message)
                .build();
    }

    public static <T> Result<T> error(int code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .build();
    }
} 