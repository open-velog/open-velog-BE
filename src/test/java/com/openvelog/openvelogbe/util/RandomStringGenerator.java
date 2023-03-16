package com.openvelog.openvelogbe.util;

import java.util.Random;

public class RandomStringGenerator {
    private static final String DIGITS = "0123456789";
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SPECIAL_CHARACTERS = "$@$!%*#?&";
    private static final String KOREAN_LETTERS = "가-힣ㄱ-ㅎㅏ-ㅣ";
    private static final Random RANDOM = new Random();

    public static String generateMemberId() {
        // Generate a random string with at least one digit and one lowercase letter, and a length between 6 and 16
        // This method will not strictly match the regex but covers the general requirements
        return generateRandomString(6, 16, DIGITS + LOWERCASE_LETTERS, 1, 1, 0);
    }

    public static String generateMemberName() {
        // Generate a random string with a length between 3 and 10, containing alphanumeric and Korean characters
        return generateRandomString(3, 10, UPPERCASE_LETTERS + LOWERCASE_LETTERS + DIGITS + KOREAN_LETTERS, 0, 0, 0);
    }

    public static String generateMemberPassword() {
        // Generate a random string with at least one letter, one digit, and a length of at least 8, containing special characters
        return generateRandomString(8, Integer.MAX_VALUE, UPPERCASE_LETTERS + LOWERCASE_LETTERS + DIGITS + SPECIAL_CHARACTERS, 1, 0, 1);
    }

    public static String generateMemberEmail() {
        // Generate a random email address
        String username = generateRandomString(3, 10, UPPERCASE_LETTERS + LOWERCASE_LETTERS + DIGITS, 0, 0, 0);
        String domain = generateRandomString(3, 10, LOWERCASE_LETTERS, 0, 0, 0);
        String tld = generateRandomString(2, 5, LOWERCASE_LETTERS, 0, 0, 0);

        return username + "@" + domain + "." + tld;
    }

    private static String generateRandomString(int minLength, int maxLength, String alphabet, int minDigits, int minLetters, int minSpecials) {
        int length = RANDOM.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < minDigits; i++) {
            sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        }

        for (int i = 0; i < minLetters; i++) {
            sb.append(LOWERCASE_LETTERS.charAt(RANDOM.nextInt(LOWERCASE_LETTERS.length())));
        }

        for (int i = 0; i < minSpecials; i++) {
            sb.append(SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length())));
        }

        while (sb.length() < length) {
            sb.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
        }

        // Shuffle the characters to randomize their order
        for (int i = 0; i < sb.length(); i++) {
            int j = RANDOM.nextInt(sb.length());
            char temp = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(j));
            sb.setCharAt(j, temp);
        }

        return sb.toString();
    }
}
