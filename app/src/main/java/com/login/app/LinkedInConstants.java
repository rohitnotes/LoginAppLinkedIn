package com.login.app;

public class LinkedInConstants {

    public static final String LINKEDIN_HOST       = "https://www.linkedin.com/oauth/";
    public static final String LINKEDIN_API_HOST   = "https://api.linkedin.com/";
    public static final String API_VERSION         = "v2/";

    public static final String QUESTION_MARK = "?";
    public static final String AMPERSAND = "&";
    public static final String EQUALS = "=";

    public static final String RESPONSE_TYPE_VALUE = "code";
    public static final String CLIENT_ID           = "";
    public static final String CLIENT_SECRET       = "";
    public static final String REDIRECT_URI        = "https://hellomajorproject.000webhostapp.com/auth/callback";
    /**
     * This is any string we want to use.
     * This will be used for avoid CSRF attacks.
     * You can generate as RandomString
     */
    public static final String STATE               = "E3ZYKC1T6H2yP4z";
    /**
     * URL-encoded, space-delimited list of member permissions your application
     * is requesting on behalf of the user. These must be explicitly requested. For
     * example, scope=r_liteprofile%20r_emailaddress%20w_member_social.
     * , is same as %20
     */
    /**
     * Allows to read basic information about profile, such as name
     */
    public static final String READ_BASIC_PROFILE = "r_basicprofile";
    /**
     * Enables access to email address field
     */
    public static final String READ_EMAIL_ADDRESS = "r_emailaddress";
    /**
     * Enables  to manage business company, retrieve analytics
     */
    public static final String MANAGE_COMPANY = "rw_company_admin";
    /**
     * Enables ability to share content on LinkedIn
     */
    public static final String SHARING = "w_share";
    /**
     * Manage and delete your data including your profile, posts, invitations, and messages
     */
    public static final String COMPLIANCE = "w_compliance";

    public static final String SCOPE               = "r_liteprofile%20r_emailaddress%20w_member_social";

    public static String getRequestForAuthorizationCode()
    {
        /*
         * https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id={your_client_id}&redirect_uri={redirect url}&state={any random string}&scope={permission scope string}
         */
        StringBuilder authorizationUrl = new StringBuilder(LINKEDIN_HOST);

        authorizationUrl.append(API_VERSION);
        authorizationUrl.append("authorization");
        authorizationUrl.append(QUESTION_MARK);
        authorizationUrl.append("response_type").append(EQUALS).append(RESPONSE_TYPE_VALUE);
        authorizationUrl.append(AMPERSAND).append("client_id").append(EQUALS).append(CLIENT_ID);
        authorizationUrl.append(AMPERSAND).append("redirect_uri").append(EQUALS).append(REDIRECT_URI);
        authorizationUrl.append(AMPERSAND).append("state").append(EQUALS).append(STATE);
        authorizationUrl.append(AMPERSAND).append("scope").append(EQUALS).append(SCOPE);

        System.out.println(authorizationUrl.toString());

        /*
         * https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id=77ui6rc3kewvx2&redirect_uri=https://hellomajorproject.000webhostapp.com/auth/callback&state=E3ZYKC1T6H2yP4gz&scope=r_liteprofile%20r_emailaddress%20w_member_social
         */
        return authorizationUrl.toString();
    }

    public static final String GRANT_TYPE = "authorization_code";

    public static String postRequestAccessTokenApiUrl(String authorizationCode)
    {
        /**
         * https://www.linkedin.com/oauth/v2/accessToken?grant_type=authorization_code&code=AQTJZ03GUZhslfO-RWBLicZET-1lhp2qRwZ99hszXK2WrsKiCLGm7OvRRlhvV_VSFohbD5Zo0Xezczyv3uIzNErzPPT0ocZQ_8TDqZUOuxCXlPoP2UFbRiuyOMXHMqyNMU-OcFdgSIQvSNe_dTA6gOSj_sxxaWRl0MNMXEsiTjdn7Hln_QxW7Mt8FMZwXg&redirect_uri=https://hellomajorproject.000webhostapp.com/auth/callback&client_id=77ui6rc3kewvx2&client_secret=Sho4OAgQhCIQ5Pvv
         */
        StringBuilder accessTokenUrl = new StringBuilder(LINKEDIN_HOST);

        accessTokenUrl.append(API_VERSION);
        accessTokenUrl.append("accessToken");
        accessTokenUrl.append(QUESTION_MARK);
        accessTokenUrl.append("grant_type").append(EQUALS).append(GRANT_TYPE);
        accessTokenUrl.append(AMPERSAND).append("code").append(EQUALS).append(authorizationCode);
        accessTokenUrl.append(AMPERSAND).append("redirect_uri").append(EQUALS).append(REDIRECT_URI);
        accessTokenUrl.append(AMPERSAND).append("client_id").append(EQUALS).append(CLIENT_ID);
        accessTokenUrl.append(AMPERSAND).append("client_secret").append(EQUALS).append(CLIENT_SECRET);

        System.out.println(accessTokenUrl.toString());
        return accessTokenUrl.toString();
    }
}
