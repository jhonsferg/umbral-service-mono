package com.codesoftlabs.umbral.util;

public class AppConstants {
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final int JWT_EXPIRATION = 86400000;
    public static final int JWT_REFRESH_EXPIRATION = 604800000;
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String AUTHORITY_PREFIX = "SCOPE_";
    public static final String API_V1_PATH = "/api/v1";
    public static final String AUTH_PATH = API_V1_PATH + "/auth";
    public static final String USER_PATH = API_V1_PATH + "/users";
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final String ERROR_CODE_VALIDATION = "VALIDATION_ERROR";
    public static final String ERROR_CODE_UNAUTHORIZED = "UNAUTHORIZED";
    public static final String ERROR_CODE_FORBIDDEN = "FORBIDDEN";
    public static final String ERROR_CODE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String ERROR_CODE_CONFLICT = "CONFLICT";
    public static final String ERROR_CODE_INTERNAL = "INTERNAL_ERROR";
    public static final String CACHE_USER = "users";
    public static final String CACHE_USER_SESSION = "user_sessions";
    public static final long CACHE_TTL_HOURS = 1;
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String ISO_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private AppConstants() {
        throw new AssertionError("Cannot instantiate utility class");
    }
}
