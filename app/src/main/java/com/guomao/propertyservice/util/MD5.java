package com.guomao.propertyservice.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5{

public static String Md5(String plainText) {
		StringBuffer buf = new StringBuffer("");
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
            for (byte aB : b) {
                i = aB;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

//			System.out.println("result: " + buf.toString());// 32λ�ļ���
//
//			System.out.println("result: " + buf.toString().substring(8, 24));// 16λ�ļ���

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buf.toString();
	}
}
