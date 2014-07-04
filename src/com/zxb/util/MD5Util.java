package com.zxb.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	
	public static String MD5Crypto(String str){
    	try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();
            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
                
        return "00000000000000000000000000000000";
    }
	
	private static String toHexString(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}
	
	private final static String HEX = "0123456789ABCDEF";

	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}

}
