package com.java.node.simple.bitOperation;

/**
 * 位运算
 */
public class BitOperationTest {

    public static void main(String[] args) {
        AND();
        OR();
        NOT();
        XOR();
        leftShift();
        rightShift();
//        byteIntConvertTest();
    }

    /**
     * 与：&
     * 两个位都为1时，结果才为1
     * 00=>0
     * 10=>0
     * 11=>1
     */
    private static void AND() {
        System.out.println("=====与运算====");
        int a = 0b00000011;
        int b = 0b00000010;
        System.out.println(String.format("%s\n%s\n结果:\n%s\n", formatByte(a), formatByte(b), formatByte(a & b)));
    }

    /**
     * 或：|
     * 两个位都为0时，结果才为0
     * 00=>0
     * 10=>1
     * 11=>1
     */
    private static void OR() {
        System.out.println("=====或运算====");
        int a = 0b00000011;
        int b = 0b00000010;
        System.out.println(String.format("%s\n%s\n结果:\n%s\n", formatByte(a), formatByte(b), formatByte(a | b)));
    }

    /**
     * 非：~
     * 0变1,1变0
     */
    private static void NOT() {
        System.out.println("=====非运算====");
        int a = 0b00000010;
        System.out.println(String.format("%s\n结果:\n%s\n", formatByte(a), formatByte(~a)));
    }

    /**
     * 异或：^
     * 两个位相同为0，相异为1
     * 00=>0
     * 10=>1
     * 11=>0
     */
    private static void XOR() {
        System.out.println("=====异或运算====");
        int a = 0b00000011;
        int b = 0b00000010;
        System.out.println(String.format("%s\n%s\n结果:\n%s\n", formatByte(a), formatByte(b), formatByte(a ^ b)));
    }

    /**
     * 左移： <<
     * 各二进位全部左移若干位,高位丢弃,低位补0
     */
    private static void leftShift() {
        System.out.println("=====左移位运算====");
        int a = 0b10000011;
        System.out.println(String.format("%s\n左移动3位\n%s\n", formatByte(a), formatByte(a << 3)));
        System.out.println(String.format("%s\n左移动8位\n%s\n", formatByte(a), formatByte(a << 8)));
    }

    /**
     * 右移
     * >>：带符号右移操作符，使用符号位来填充移动后左侧空出的位。如果原数是正数，则在左侧填充 0；如果原数是负数，则在左侧填充 1。
     * >>>:无符号右移操作符，它使用 0 来填充左侧空出的位，无论原数是正数还是负数。
     */
    private static void rightShift() {
        System.out.println("=====右移位运算====");
        int a = (0b10000011000000000000000000000000);
        int b = (0b01000011000000000000000000000000);
        System.out.println(String.format("%s\n负数有符号右移动3位\n%s\n", formatInt(a), formatInt(a >> 3)));
        System.out.println(String.format("%s\n正数有符号右移动3位\n%s\n", formatInt(b), formatInt(b >> 3)));
        System.out.println(String.format("%s\n负数无符号右移动3位\n%s\n", formatInt(a), formatInt(a >>> 3)));
        System.out.println(String.format("%s\n正数无符号右移动3位\n%s\n", formatInt(b), formatInt(b >>> 3)));
    }

    /**
     * bit转换测试
     */
    private static void byteIntConvertTest() {
        //00000000000000000000000011111111 = 255
        int i1 = 0b11111111;
        //00000000000000000000000001111111 = 127
        int i2 = 0b01111111;
        //11111111 = -1
        byte b1 = (byte) i1;
        //01111111 = 127
        byte b2 = (byte) i2;
        //11111111111111111111111111111111 = -1
        int i3 = (int) b1;
        //00000000000000000000000001111111 = 127
        int i4 = (int) b2;
        //结果为-1（11111111111111111111111111111111），因为显示byte运算得到了11111111，即-1，然后转为了int，得到了-1
        System.out.println(i1);//255
        System.out.println(b1);//-1
        System.out.println(i3);//-1

        System.out.println(i2);//127
        System.out.println(b2);//127
        System.out.println(i4);//127
        // 结论1：int转byte，会可能出现位错误，但是byte转int不会

        //11111111&11111111=11111111=-1
        int b1b1 = b1 & b1;
        System.out.println(b1b1);//-1
        //结论2：byte之间位运算，先运算得到新byte，后转为int

        //11111111&00000000000000000000000011111111=00000000000000000000000011111111=255
        int b1i1 = b1 & i1;
        System.out.println(b1i1);//255
        //结论3：byte与int位运算，先byte转int，再运算int与int运算得到新int

        int b1_1 = b1 >>> 1;//31个1
        int b1_2 = b1 >>> ((byte) 1);//31个1
        int b1_3 = b1 >> 1;//32个1
        int b1_4 = b1 >> ((byte) 1);//32个1
        //结论4：byte进行移位，统一转为了int再移位的
    }

    private static String formatByte(int b) {
        return (Integer.toBinaryString((b & 0xff) + 0x100).substring(1));
    }

    private static String formatInt(int a) {
        String s = Integer.toBinaryString(a);
        while (s.length() < 32) {
            s = 0 + s;
        }
        return s;
    }
}
