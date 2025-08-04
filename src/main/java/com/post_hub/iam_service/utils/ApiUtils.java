package com.post_hub.iam_service.utils;

import com.post_hub.iam_service.model.constants.ApiConstants;

public class ApiUtils {

    public static String getMethodName() {
        try {
            return Thread.currentThread().getStackTrace()[1].getMethodName();
            // [1] getMethodName() метод, который вызвал текущий
        } catch (Exception cause) {
            return ApiConstants.UNDEFINED;
        }
    }
}
