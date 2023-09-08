package com.base.short2long.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class LoUtils {

    public static byte[] swapBytes(byte[] byteArray) {
        byte[] swappedArray = Arrays.copyOf(byteArray, byteArray.length);

        // 交换1字节和3字节
        byte temp = swappedArray[0];
        swappedArray[0] = swappedArray[2];
        swappedArray[2] = temp;

        temp = swappedArray[1];
        swappedArray[1] = swappedArray[3];
        swappedArray[3] = temp;

        return swappedArray;
    }
    public static String byte2HexStr(byte[] b) {
        StringBuffer hs = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs.append("0" + stmp);
            else
                hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
        }
        return ret;
    }

    private static byte uniteBytes(String src0, String src1) {
        byte b0 = Byte.decode("0x" + src0).byteValue();
        b0 = (byte) (b0 << 4);
        byte b1 = Byte.decode("0x" + src1).byteValue();
        byte ret = (byte) (b0 | b1);
        return ret;
    }

    public static byte[] shortToBytes(short val) {
        byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            b[i] = (byte) (val >>> (8 - i * 8));
        }
        return b;
    }

    /**
     * 将字节数组转为整型
     *
     * @param num
     * @return
     */
    public static int bytesToShort(byte[] arr) {
        int mask = 0xFF;
        int temp = 0;
        int result = 0;
        for (int i = 0; i < 2; i++) {
            result <<= 8;
            temp = arr[i] & mask;
            result |= temp;
        }
        return result;
    }


    /***
     *
     * int Asc2Bcd( char *sDest, char *sSrc, int nLen, char cAlign )
     * {
     *     int  nSrcOffset, nDestOffset, nDigit;
     *
     *     nDestOffset = ( cAlign == 'R' ) && ( nLen % 2 );
     *
     *     for( nSrcOffset = 0; nSrcOffset < nLen; nSrcOffset++, nDestOffset++ )
     *     {
     *         nDigit = sSrc[nSrcOffset] - '0';
     *
     *         if( nDestOffset % 2 )
     *             sDest[nDestOffset/2] |= nDigit;
     *         else
     *             sDest[nDestOffset/2] = nDigit << 4;
     *
     *     }
     *
     *     return (nLen + 1) / 2;
     *
     * }
     * @param s
     * @return
     */
    public static byte[] StrToBCDBytes(String s) {
        if (s.length() % 2 != 0) {
            s = "0" + s;
        }
/*		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		char[] cs = s.toCharArray();
		for (int i = 0; i < cs.length; i += 2) {
//			int high = cs[i] - 48;
//			int low = cs[i + 1] - 48;
 			int high = asc_to_bcd(cs[i]);
			int low = asc_to_bcd(cs[i + 1]);
			baos.write(high << 4 | low);
		}
		return baos.toByteArray();*/
        return ASCII_To_BCD(s.getBytes(), s.length());
    }


    private static byte asc_to_bcd(char asc) {
        byte bcd;

        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }

    private static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd((char) ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd((char) ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    public static String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;

        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }


    public static byte[] StrToBCDBytesRight(String s) {
        if (s.length() % 2 != 0) {
            s = s + "0";
        }
/*		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		char[] cs = s.toCharArray();
		for (int i = 0; i < cs.length; i += 2) {
//			int high = cs[i] - 48;
//			int low = cs[i + 1] - 48;
			int high = asc_to_bcd(cs[i]);
			int low = asc_to_bcd(cs[i + 1]);
			baos.write(high << 4 | low);
		}
		return baos.toByteArray();*/
        return ASCII_To_BCD(s.getBytes(), s.length());
    }

    public static byte[] getAsciiBytes(String input) {
        char[] c = input.toCharArray();
        byte[] b = new byte[c.length];
        for (int i = 0; i < c.length; i++)
            b[i] = (byte) (c[i] & 0x007F);

        return b;
    }

    static byte[] Asc2Hex(String sSource) {
        return ASCII_To_BCD(sSource.getBytes(), sSource.length());
    }


    public static String bcdTostr(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            int h = ((b[i] & 0xff) >> 4) + 48;
            sb.append((char) h);
            int l = (b[i] & 0x0f) + 48;
            sb.append((char) l);
        }
        return sb.toString();
    }

    public static int bitmap_test(byte[] bitmap, int bit) {
        return (bitmap[bitmap_index(bit)] & bitmap_mask(bit));
    }

    public static byte[] bitmap_clr(byte[] bitmap, int bit) {
        bitmap[bitmap_index(bit)] &= ~bitmap_mask(bit);
        return bitmap;
    }

    public static byte[] bitmap_set(byte[] bitmap, int bit) {
        bitmap[bitmap_index(bit)] |= bitmap_mask(bit);
        return bitmap;
    }


    public static int bitmap_index(int bit) {
        return (((bit) - 1) >> 3);
    }

    public static int bitmap_mask(int bit) {
        return (0x80 >> (((bit) - 1) & 0x07));
    }

    public static boolean[] getBinaryFromByte(byte[] b) {
        boolean[] binary = new boolean[b.length * 8 + 1];
        String strsum = "";
        for (int i = 0; i < b.length; i++) {
            strsum += getEigthBitsStringFromByte(b[i]);
        }
        for (int i = 0; i < strsum.length(); i++) {
            if (strsum.substring(i, i + 1).equalsIgnoreCase("1")) {
                binary[i + 1] = true;
            } else {
                binary[i + 1] = false;
            }
        }
        return binary;
    }

    public static String getEigthBitsStringFromByte(int b) {
        b |= 256;
        String str = Integer.toBinaryString(b);
        int len = str.length();
        return str.substring(len - 8, len);
    }

    public static int bcdToint(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            int h = ((b[i] & 0xff) >> 4) + 48;
            sb.append((char) h);
            int l = (b[i] & 0x0f) + 48;
            sb.append((char) l);
        }
        return Integer.parseInt(sb.toString());
    }

    public static int numlen2bcdlen(int s) {
        int bcdlen = 0;
        if (s % 2 == 0) {
            bcdlen = s / 2;
        } else {
            bcdlen = (s + 1) / 2;
        }
        return bcdlen;

    }

    public final static String DEFAULT_ENCODE = "GBK";//

    public static String asciiToString(byte[] value) {
        try {
            return new String(value, DEFAULT_ENCODE);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static byte[] getByteFromBinary(boolean[] binary) {
        int num = (binary.length - 1) / 8;
        if ((binary.length - 1) % 8 != 0) {
            num = num + 1;
        }
        byte[] b = new byte[num];
        String s = "";
        for (int i = 1; i < binary.length; i++) {
            if (binary[i]) {
                s += "1";
            } else {
                s += "0";
            }
        }
        String tmpstr;
        int j = 0;
        for (int i = 0; i < s.length(); i = i + 8) {
            tmpstr = s.substring(i, i + 8);
            b[j] = getByteFromEigthBitsString(tmpstr);
            j = j + 1;
        }
        return b;
    }

    public static byte getByteFromEigthBitsString(String str) {
        byte b;
        if (str.substring(0, 1).equals("1")) {
            str = "0" + str.substring(1);
            b = Byte.valueOf(str, 2);
            b |= 128;
        } else {
            b = Byte.valueOf(str, 2);
        }
        return b;
    }

    /**
     * ********************************************************
     *
     * @param b
     * @return byte[]
     * @Title: assemble
     * @Description: 灏嗗涓猙yte鏁扮粍  缁勫悎鎴愪竴涓猙yte鏁扮粍
     * @date 2013-5-4 涓嬪崍05:49:44
     * *******************************************************
     */
    public static byte[] assemble(byte[]... b) {
        int length = 0;
        try {
            for (byte[] bl : b) {
                length += bl.length;
            }
            byte[] data = new byte[length];
            int count = 0;
            for (int i = 0; i < b.length; i++) {
                System.arraycopy(b[i], 0, data, count, b[i].length);
                count += b[i].length;
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertStringToHex(byte[] chars) {


        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            String temp = Integer.toHexString((int) chars[i] & 0x0FF);
//			hex.append(temp.length()<2?"0"+temp:temp);
            hex.append(String.format("%02x", chars[i] & 0x0FF));
        }

        return hex.toString();
    }

    public static byte[] convertHexToString(String hex) {
        byte[] retbyte = new byte[hex.length() / 2];
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {
            //grab the hex in
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal

            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            retbyte[i / 2] = (byte) (decimal);

        }

        return retbyte;
    }


    public static void main(String[] args) throws Exception {

        byte[] b = convertHexToString("9F2608B00FAE806BB8759C9F2706809F101307010103A00000010A0100000000006119E9889F3704C1E772549F360200A0950500000000009A031811079C01009F02060000000036005F2A02015682027C009F1A0201569F03060000000000009F330390C8C09F34030000009F3501229F1E0831323334353637388408A0000003330101019F090200209F410400000001");
        System.out.println(convertStringToHex(b));//E6B299E58F91E997AEE53F    //
    }

}