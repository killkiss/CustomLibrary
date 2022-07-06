package svnt.com.utlib;

public class MyClass {

    public static void main(String[] s) {
        String data = "F31A-65BE-47FE-A20F";
        String [] dataList = data.split("-");
        for (int i = 0; i < dataList.length; i++) {
            int intHex = hexStringToAlgorism(dataList[i]);
            System.out.println(intHex + "");
        }
    }

    /**
     * 16进制转10进制
     *
     * @param hex 16进制字符串
     * @return 10进制字符串
     */
    public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            int cResult = result;
            cResult += Math.pow(16, max - i) * algorism;
            if (cResult < 2147483647) {
                result = cResult;
            }

        }
        return result;
    }
}
