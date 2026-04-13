package com.zjgsu.teamplatform.common;

/**
 * 系统常量。
 */
public final class Constants {
    private Constants() {
    }

    public static final String HEADER_USER_ID = "X-User-Id";

    public static final String TEAM_STATUS_ACTIVE = "ACTIVE";
    public static final String TEAM_STATUS_CLOSED = "CLOSED";

    public static final String MEMBER_ROLE_CREATOR = "CREATOR";
    public static final String MEMBER_ROLE_ADMIN = "ADMIN";
    public static final String MEMBER_ROLE_MEMBER = "MEMBER";

    public static final String REQUEST_STATUS_PENDING = "PENDING";
    public static final String REQUEST_STATUS_APPROVED = "APPROVED";
    public static final String REQUEST_STATUS_REJECTED = "REJECTED";

    public static final String INVITATION_STATUS_PENDING = "PENDING";
    public static final String INVITATION_STATUS_ACCEPTED = "ACCEPTED";
    public static final String INVITATION_STATUS_REJECTED = "REJECTED";

    public static final String NOTIFICATION_TYPE_JOIN_REQUEST = "JOIN_REQUEST";
    public static final String NOTIFICATION_TYPE_SYSTEM = "SYSTEM";
    public static final String NOTIFICATION_TYPE_TEAM_UPDATE = "TEAM_UPDATE";
}
