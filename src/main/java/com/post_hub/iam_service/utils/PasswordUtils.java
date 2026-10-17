package com.post_hub.iam_service.utils;

import com.post_hub.iam_service.model.constants.ApiConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PasswordUtils {

    public static boolean isNotValidPassword(String password) {
        if (password == null || password.isEmpty() || password.trim().isEmpty()) {
            /*
                password.trim().isEmpty() выполняет проверку, является ли
                 строка password "пустой" после удаления пробельных символов в начале и конце строки.
             */
            return true;
        }
        String trim = password.trim();
        if (trim.length() < ApiConstants.REQUIRED_MIN_PASSWORD_LENGTH) {
            return true;
        }
        int charactersNumber = ApiConstants.REQUIRED_MIN_CHARACTERS_NUMBER_IN_PASSWORD;
        int lettersUCaseNumber = ApiConstants.REQUIRED_MIN_LETTERS_NUMBER_EVERY_CASE_IN_PASSWORD;
        int lettersLCaseNumber = ApiConstants.REQUIRED_MIN_LETTERS_NUMBER_EVERY_CASE_IN_PASSWORD;
        int digitsNumber = ApiConstants.REQUIRED_MIN_DIGITS_NUMBER_IN_PASSWORD;
        for (int i = 0; i < trim.length(); i++) {
            String currentLetter = String.valueOf(trim.charAt(i));
            // charAt(i) в этом коде извлекает один символ из строки trim на позиции i
            if (!ApiConstants.PASSWORD_ALL_CHARACTERS.contains(currentLetter)) {
                 /*
                    сравниваем допустимые символы и текущий пароль, если
                    пароль содержит допустимый символ -> возвращаем false и не заходим в if.
                 */
                return true;
            }
            charactersNumber -= ApiConstants.PASSWORD_CHARACTERS.contains(currentLetter) ? 1 : 0;
            lettersUCaseNumber -= ApiConstants.PASSWORD_LETTERS_UPPER_CASE.contains(currentLetter) ? 1 : 0;
            lettersLCaseNumber -= ApiConstants.PASSWORD_LETTERS_LOWER_CASE.contains(currentLetter) ? 1 : 0;
            digitsNumber -= ApiConstants.PASSWORD_DIGITS.contains(currentLetter) ? 1 : 0;
        }
        return ((charactersNumber > 0) || (lettersUCaseNumber > 0) || (lettersLCaseNumber > 0) || (digitsNumber > 0));
    }

}
