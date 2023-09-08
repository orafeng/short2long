package com.base.short2long.utils;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class CommonFunction {


    public static String fillString(String string, String charset, char filler,
                                    int totalLength, boolean atEnd) {
        if (charset == null)
            charset = "gbk";
        try {
            if (string == null)
                string = "";
            byte[] tempbyte = string.getBytes(charset);
            int currentLength = tempbyte.length;
            int delta = totalLength - currentLength;
            for (int i = 0; i < delta; i++) {
                if (atEnd) {
                    string += filler;
                } else {
                    string = filler + string;
                }
            }
            return string;
        } catch (Exception ex) {
            return string;
        }
    }

    /**
     * 字符截取
     *
     * @param value
     * @param length
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String cutString(String value, int length)
            throws UnsupportedEncodingException {

        // 为NULL 则返回空
        if (value == null)
            return "";

        // 如果数值的字节数小于传过来的字节数直接返回改值
        if (value.getBytes("gbk").length <= length)
            return value;

        // 超过则做截取
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < value.length(); i++) {
            String one = value.substring(i, i + 1);
            int tmpLen = (sb.toString() + one).getBytes("gbk").length;
            if (tmpLen > length)
                return sb.toString();
            sb.append(one);
        }
        return sb.toString();
    }

    /**
     * 字符补充
     *
     * @param data
     * @param filler
     * @param length
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String fixString(String data, char filler, int length)
            throws UnsupportedEncodingException {
        String s = fillString(data, "gbk", filler, length, true);
        s = cutString(s, length);
        // data 不足则补冲
        s = fillString(s, "gbk", filler, length, true);
        return s;
    }

    public static String substring(String data, String charset, int begin,
                                   int end) throws UnsupportedEncodingException {

        byte[] b = data.getBytes(charset);
        byte[] tmp = new byte[end - begin];

        System.arraycopy(b, begin, tmp, 0, end - begin);

        return new String(tmp, charset);
    }

    public static byte[] subbyte(byte[] data, int begin, int end)
            throws UnsupportedEncodingException {

        end = Math.min(data.length, end);
        byte[] tmp = new byte[end - begin];
        System.arraycopy(data, begin, tmp, 0, end - begin);
        return tmp;

    }

    /**
     * @param data
     * @param begin
     * @param end
     * @param blank
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] subbyte(byte[] data, int begin, int end, byte blank)
            throws UnsupportedEncodingException {

        end = Math.min(data.length - 1, end);
        int trueEnd = end;
        for (int i = begin; i <= end; i++) {
            if (data[i] == blank) {
                trueEnd = i - 1;
            }
        }
        if (trueEnd < begin)
            trueEnd = begin;

        byte[] tmp = new byte[trueEnd - begin];

        System.arraycopy(data, begin, tmp, 0, trueEnd - begin);

        return tmp;
    }

    public static String substring(String data, String charset, int begin,
                                   int end, byte blank) throws UnsupportedEncodingException {

        byte[] tmp = subbyte(data.getBytes(charset), begin, end, blank);
        return new String(tmp, charset);
    }

    public static List<String> readProperties(String fileName)
            throws IOException {
        InputStream inputStream = new FileInputStream(fileName);
        InputStreamReader fileReader = new InputStreamReader(inputStream, "gbk");
        BufferedReader bufferReader = new BufferedReader(fileReader);

        List<String> lines = new LinkedList<String>();
        String str = null;

        while ((str = bufferReader.readLine()) != null) {
            if (!str.trim().equals("")) {
                if (str.charAt(0) == '#')
                    continue;
                lines.add(str);
            }
        }
        bufferReader.close();
        return lines;
    }


    /**
     * 遍历所有文件名
     *
     * @param path
     * @param fileType
     * @return
     */
    public static List<String> listFiles(String path, String fileType) {

        List<String> retList = new LinkedList<String>();
        File[] files = new File(path).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith("." + fileType)) {
                retList.add(files[i].getName());
            }
        }
        return retList;
    }

    /**
     * 金额转换
     *
     * @param amt
     * @return
     */
    public static String getDBFormatAmt(String amt) {
        if (amt == null || amt.trim().equals(""))
            return "";
        amt = amt.replaceAll(",", "");
        BigDecimal bigamt = new BigDecimal(amt);
        bigamt = bigamt.multiply(new BigDecimal(100));
        String tmp = bigamt.toString();
        int pos = tmp.indexOf(".");
        if (pos != -1)
            tmp = tmp.substring(0, pos);
        return tmp;
    }


    /**
     * @param amt
     * @return
     */
    public static String getStdFormatAmtNoComma(String amt) {
        if (amt == null)
            return "";
        if (amt.length() == 1)
            return "0.0" + amt;
        if (amt.length() == 2)
            return "0." + amt;
        if (amt.length() > 2)
            return transAmtStringNoComma(amt.substring(0, amt.length() - 2))
                    + "." + amt.substring(amt.length() - 2);
        return "";
    }

    private static String transAmtStringNoComma(String str) {
        if (str == null)
            return "";
        StringBuffer strb = new StringBuffer(str);
        strb = strb.reverse();
        StringBuffer strr = new StringBuffer();
        for (int i = 0; i < strb.length(); i++) {
            strr.append(strb.charAt(i));
        }
        strr = strr.reverse();
        return strr.toString();
    }


    /**
     * @param b
     * @return
     */
    public static String printMsg(byte[] b) {

        try {

            int numPerLine = 16;
            int numPerBlock = 8;

            StringBuffer print = new StringBuffer("\n");
            print.append("length=" + b.length + "\n");

            // ascii
            StringBuffer printMsg = new StringBuffer();
            // 16进制
            StringBuffer printCode = new StringBuffer();

            for (int i = 0; i < b.length; i++) {
                char c = (char) b[i];
                if ((c >= 0 && c <= 31) || c >= 127) {
                    c = '.';
                }

                printMsg.append(c);
                String hex = Integer.toHexString((int) b[i] & 0xFF);
                if (hex.length() == 1) {
                    printCode.append("0");
                }
                printCode.append(hex);
                printCode.append(" ");

                if (i % numPerLine == numPerLine - 1) {
                    print.append(CommonFunction.fillString(
                            Integer.toString(i + 1 - numPerLine), null, '0', 4,
                            false));
                    print.append(": ");
                    print.append(printCode);
                    print.append("\t");
                    print.append(printMsg);
                    print.append("\n");
                    printMsg = new StringBuffer();
                    printCode = new StringBuffer();
                } else if (i % numPerBlock == numPerBlock - 1) {
                    printMsg.append(" ");
                    printCode.append(" ");
                } else if (i == b.length - 1) {
                    print.append(CommonFunction.fillString(
                            Integer.toString(i / numPerLine * numPerLine),
                            null, '0', 4, false));
                    print.append(": ");

                    int fillBlankTimes = numPerLine - b.length % numPerLine;

                    for (int j = 0; j < fillBlankTimes; j++) {
                        printCode.append("   ");
                    }
                    if (fillBlankTimes >= numPerBlock)
                        printCode.append(" ");

                    print.append(printCode);
                    print.append("\t");

                    for (int j = 0; j < fillBlankTimes; j++) {
                        print.append(" ");
                    }
                    if (fillBlankTimes >= numPerBlock)
                        printCode.append(" ");

                    print.append(printMsg);
                    print.append("\n");
                    printMsg = new StringBuffer();
                    printCode = new StringBuffer();
                }
            }

            return print.toString();
            // return "";
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }


    /**
     * @param configName
     * @return
     */
    public static String getConigDir(String configName) {
        String configDir = System.getProperty(configName);
        if (configDir == null) {
            configDir = System.getenv(configName);
        }
        if (configDir == null) {
            configDir = CommonFunction.class.getClassLoader().getResource("")
                    .getPath();
        }
        if (configDir.startsWith("file:")) {
            configDir = configDir.replaceFirst("file:", "");
        }
        return configDir;
    }


    /**
     * 字节传16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);//
    }

    public static void main(String[] args) {
        try {
            System.out
                    .println(printMsg(new byte[]{0x01, 0x02, 0x07, 0x10,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x01, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x01, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00}));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}