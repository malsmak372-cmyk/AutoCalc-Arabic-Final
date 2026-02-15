package com.dreamfish.com.autocalc.utils;

import java.util.Base64;


public class Base64Utils {

    
    public static String encode(String message){
        if (message == null){
            return null;
        }
        byte[] bytes = message.getBytes();
        byte[] result = Base64.getEncoder().encode(bytes);
        return new String(result);
    }

    
    public static byte[] encode(byte[] bytes){
        return Base64.getEncoder().encode(bytes);
    }

    
    public static String decode(String message){
        if (message == null){
            return null;
        }
        byte[] bytes = message.getBytes();
        byte[] result = Base64.getDecoder().decode(bytes);
        return new String(result);
    }

    
    public static byte[] decode(byte[] bytes){
        return Base64.getDecoder().decode(bytes);
    }
}
