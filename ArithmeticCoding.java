import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class section {// 定义一个区间类
    BigDecimal low;
    BigDecimal high;

    section() {
    }

    section(BigDecimal low, BigDecimal high) {
        this.low = low;
        this.high = high;
    }
}

public class ArithmeticCoding {
    private static Map<Character, section> intervalDistributionTable = new HashMap<>();
	private static int precision = 3;	//字符概率精度

    public static BigDecimal encode(StringBuilder str) {

        Map<Character, Integer> probabilityTable = new HashMap<>();

        int len = str.length();
        // 获取各个字符的出现频率
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (probabilityTable.containsKey(ch)) {
                probabilityTable.replace(ch, probabilityTable.get(ch) + 1);
            } else {
                probabilityTable.put(ch, 1);
            }
        }
        // 初始化区间分布表
        BigDecimal currentPosition = new BigDecimal("0");
        for (Character ch : probabilityTable.keySet()) {
            BigDecimal probability = new BigDecimal(probabilityTable.get(ch)).divide(new BigDecimal(len), precision,
                    BigDecimal.ROUND_HALF_UP);
            section sec = new section(currentPosition, currentPosition.add(probability));
            intervalDistributionTable.put(ch, sec);
            currentPosition = currentPosition.add(probability);
        }

        // 下面开始正式的编码工作
        // 使用公式: low = low + (high - low) * L
        // high = low + (high - low) *H 进行迭代
        BigDecimal low = new BigDecimal("0");
        BigDecimal high = new BigDecimal("1");
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            section sec = intervalDistributionTable.get(ch);
            BigDecimal lowTemp = low.add(high.subtract(low).multiply(sec.low));
            BigDecimal highTemp = low.add(high.subtract(low).multiply(sec.high));
            low = lowTemp;
            high = highTemp;
        }

        return low;
    }

    public static String decode(BigDecimal code, int len, Map<Character, section> IDT) {
        StringBuilder sbd = new StringBuilder();
        for (int i = 0; i < len; i++) {
            for (Entry<Character, section> cEntry : IDT.entrySet()) {
                section sec = cEntry.getValue();
                if (code.compareTo(sec.high) < 0 && code.compareTo(sec.low) >= 0) {
                    sbd.append(cEntry.getKey());
                    code = code.subtract(sec.low).divide(sec.high.subtract(sec.low));
                    break;
                }
            }
        }
        return new String(sbd);
    }

    public static void main(String[] args) {
        StringBuilder sbd = new StringBuilder(
                "abcdefghijklmnaksjdcksdjbcksdjbckbdsjhbjavbkhdsbavj34807171048904065481234567890[]``,./;<>?|~!@#$%^&*())'khbdjscbjasdhbcjabdcjkhabcjbakbcjdaABCDEFGHIJKLMNOPQRSTUVWXYZskcbjkahbdcjhbsajkcbjsabchjadscbjaskbckjenhgjklskrjehgsjgekjhgjkesrkgaabbbcccjdjdjjgdyujgzucgycxvzytrverwqqwtvipnoivnbiuncouinjfdklgshgkjsherkjgesrghsergdjdjdjdjdjd");

        System.out.println("len = " + sbd.length() + "\n");
        BigDecimal code = encode(sbd);
        System.out.println("code = " + code + "\n");
        System.out.println("source string = " + sbd);
        System.out.println("decode string = " + decode(code, sbd.length(), intervalDistributionTable));
    }
}