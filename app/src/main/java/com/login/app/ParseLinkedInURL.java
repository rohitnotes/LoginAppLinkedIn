package com.login.app;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ParseLinkedInURL
{
    public static String parseAuthorizationUrlAndGetAuthCode(String stringUrl)
    {
        URL url;
        String authorizationCode = "";

        try {

            url = new URL(stringUrl);

            System.out.println("protocol = " + url.getProtocol());
            System.out.println("authority = " + url.getAuthority());
            System.out.println("host = " + url.getHost());
            System.out.println("port = " + url.getPort());
            System.out.println("path = " + url.getPath());
            System.out.println("query = " + url.getQuery());
            System.out.println("filename = " + url.getFile());
            System.out.println("ref = " + url.getRef());

            Map<String, String> map = getQueryMap(url.getQuery());
           /* Set<String> keys = map.keySet();
            for (String key : keys)
            {
                System.out.println("Key = " + key);
                System.out.println("Value = " + map.get(key));
            }*/

            Map.Entry<String,String> entry = map.entrySet().iterator().next();
            String key= entry.getKey();
            String value=entry.getValue();
            authorizationCode=value;
        }
        catch (MalformedURLException e)
        {
            System.out.println("Malformed URL: " + e.getMessage());
        }
        return authorizationCode;
    }

    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }
}
