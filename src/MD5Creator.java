/**
 * 输入给定字符串，生成MD5信息摘要
 * 通过对比，答案正确可靠
 */

public class MD5Creator {
    private static String originalData;    //原始数据
    private static String MD5;            //最终结果

    //四个幻数
    private static final int magicNumA = 0x67452301;
    private static final int magicNumB = 0xefcdab89;
    private static final int magicNumC = 0x98badcfe;
    private static final int magicNumD = 0x10325476;
    private static int tempA, tempB, tempC, tempD;

    //常量ti  floor((abs(sin(i+1))*(2pow32)))
    private static final int[] k = new int[]{
            0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee,
            0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501, 0x698098d8,
            0x8b44f7af, 0xffff5bb1, 0x895cd7be, 0x6b901122, 0xfd987193,
            0xa679438e, 0x49b40821, 0xf61e2562, 0xc040b340, 0x265e5a51,
            0xe9b6c7aa, 0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
            0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed, 0xa9e3e905,
            0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a, 0xfffa3942, 0x8771f681,
            0x6d9d6122, 0xfde5380c, 0xa4beea44, 0x4bdecfa9, 0xf6bb4b60,
            0xbebfbc70, 0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05,
            0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665, 0xf4292244,
            0x432aff97, 0xab9423a7, 0xfc93a039, 0x655b59c3, 0x8f0ccc92,
            0xffeff47d, 0x85845dd1, 0x6fa87e4f, 0xfe2ce6e0, 0xa3014314,
            0x4e0811a1, 0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391};
    //向左移动的位数
    private static final int[] s = new int[]{7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7,
            12, 17, 22, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
            4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 6, 10,
            15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21};

    private static void init() {
        tempA = magicNumA;
        tempB = magicNumB;
        tempC = magicNumC;
        tempD = magicNumD;
    }

    private static void MainLoop(int M[]) {
        int F, g;
        int a = tempA;
        int b = tempB;
        int c = tempC;
        int d = tempD;
        for (int i = 0; i < 64; i++) {
            if (i < 16) {
                F = (b & c) | ((~b) & d);
                g = i;
            } else if (i < 32) {
                F = (d & b) | ((~d) & c);
                g = (5 * i + 1) % 16;
            } else if (i < 48) {
                F = b ^ c ^ d;
                g = (3 * i + 5) % 16;
            } else {
                F = c ^ (b | (~d));
                g = (7 * i) % 16;
            }
            int tmp = d;
            d = c;
            c = b;
            b = b + leftRotate(a + F + k[i] + M[g], s[i]);
            a = tmp;
        }
        tempA = a + tempA;
        tempB = b + tempB;
        tempC = c + tempC;
        tempD = d + tempD;

    }

    //循环左移函数 number 循环左移 bit 位
    private static int leftRotate(int number, int bit) {
        return (number << bit) | (number >>> (32 - bit));
    }


    /**
     * 填充函数
     * 处理后应满足bits≡448(mod512),字节就是bytes≡56（mode64)
     * 填充方式为先加一个0,其它位补零
     * 最后加上64位的原来长度
     */
    private static int[] add(String info) {
        int num = ((info.length()+8)/64)+1; //以512位，64个字节为1组
        int[] strByte = new int[num*16];    //每4个字节构成一个32位整数，64/4=16 所以有16个整数
        for(int i=0;i<num*16;i++){          //全部初始化0
            strByte[i]=0;
        }
        int i;
        for(i=0;i<info.length();i++){
            strByte[i>>2]|=info.charAt(i)<<((i%4)*8);//一个整数存储四个字节，小端序
        }
        strByte[i>>2] |= 0x80<<((i%4)*8);            //尾部添加1
        strByte[num*16-2] = info.length()*8;         //添加原长度，长度指位的长度，所以要乘8，放在倒数第二个,这里长度只用了32位
        return strByte;
    }

    //返回结果
    public static String getMD5(String info) {
        init();
        originalData = info;
        int strByte[]=add(originalData);
        for(int i=0;i<strByte.length/16;i++){
            int num[]=new int[16];
            for(int j=0;j<16;j++){
                num[j]=strByte[i*16+j];
            }
            MainLoop(num);
        }
        MD5 =  changeHex(tempA)+changeHex(tempB)+changeHex(tempC)+changeHex(tempD);
        return MD5;
    }
    /*
    *整数变成16进制字符串
    */
    private static String changeHex(int a){
        String str="";
        for(int i=0;i<4;i++){
            str+=String.format("%2s", Integer.toHexString(((a>>i*8)%(1<<8))&0xff)).replace(' ', '0');

        }
        return str.toUpperCase();
    }

    /**
     * 以下为一致性检测
     * 对比标准类：java.security.MessageDigest
     * 检测结果：完全一致
     * */
    /*
    public static void main(String[] args) {
        String md5str = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = md.digest("abc".getBytes());
            md5str = bytesToHex(buffer);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println("Correct:" + md5str);
        md5str = MD5Creator.getMD5("abc");
        System.out.println("Unknown:" + md5str);
    }
    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        // 把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }*/
}
