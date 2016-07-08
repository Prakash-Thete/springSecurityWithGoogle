package com.util

/**
 * Created by KS115 on 3/9/16.
 */
class ServiceContext {
    String  userId
    String  userFullName
    String  mainRole
    String  emailId
    boolean forceUserToResetPassword
    Date    triggeredPasswordReset
    Long    companyId
    Long    siteId
    String  userAgent
    String  userDevice
    String  host


    @Override
    public String toString() {
        return "ServiceContext{" +
                "userId='" + userId + '\'' +
                ", userFullName='" + userFullName + '\'' +
                ", mainRole='" + mainRole + '\'' +
                ", emailId='" + emailId + '\'' +
                ", forceUserToResetPassword=" + forceUserToResetPassword +
                ", triggeredPasswordReset=" + triggeredPasswordReset +
                ", companyId=" + companyId +
                ", siteId=" + siteId +
                ", userAgent='" + userAgent + '\'' +
                ", userDevice='" + userDevice + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
