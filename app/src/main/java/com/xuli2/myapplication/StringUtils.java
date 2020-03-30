package com.xuli2.myapplication;

public class StringUtils {
    public static String getUTFStringByEncoding(String str) {
        String encode = "UTF-8";
        String returnStr = "";
        try {
            if(str!=null){
                if (str.equals(new String(str.getBytes("GB2312"), "GB2312"))) {
                    encode = "GB2312";
                }else if (str.equals(new String(str.getBytes("ISO-8859-1"), "ISO-8859-1"))) {
                    encode = "ISO-8859-1";
                }else if (str.equals(new String(str.getBytes("UTF-8"), "UTF-8"))) {
                    encode = "UTF-8";
                }else if (str.equals(new String(str.getBytes("GBK"), "GBK"))) {
                    encode = "GBK";
                }
                if(encode.equals("UTF-8")){
                    returnStr = str;
                }else{
                    returnStr = new String(str.getBytes(encode),"UTF-8");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return returnStr;
    }
}
