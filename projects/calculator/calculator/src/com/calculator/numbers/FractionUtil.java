package com.calculator.numbers;

import java.math.BigInteger;
import java.util.*;

/**
 * 分数工具类，提供数组运算、统计功能：
 * 求和、乘积、平均、最大值、最小值、排序、方差、标准差、
 * 中位数、众数、几何平均、调和平均、变异系数、随机生成等。
 */
public class FractionUtil {

    /** 求和 */
    public static Fraction sum(Fraction... fractions) {
        Fraction result = Fraction.ZERO();
        for (Fraction f : fractions) result = result.add(f);
        return result;
    }

    /** 求乘积 */
    public static Fraction product(Fraction... fractions) {
        Fraction result = Fraction.ONE();
        for (Fraction f : fractions) result = result.multiply(f);
        return result;
    }

    /** 平均值 */
    public static Fraction average(Fraction... fractions) {
        if (fractions.length == 0) throw new ArithmeticException("空数组无法求平均");
        Fraction total = sum(fractions);
        return total.divide(new Fraction(BigInteger.valueOf(fractions.length)));
    }

    /** 最大值 */
    public static Fraction max(Fraction... fractions) {
        if (fractions.length == 0) throw new ArithmeticException("空数组无最大值");
        Fraction max = fractions[0];
        for (int i = 1; i < fractions.length; i++) {
            if (fractions[i].compareTo(max) > 0) max = fractions[i];
        }
        return max;
    }

    /** 最小值 */
    public static Fraction min(Fraction... fractions) {
        if (fractions.length == 0) throw new ArithmeticException("空数组无最小值");
        Fraction min = fractions[0];
        for (int i = 1; i < fractions.length; i++) {
            if (fractions[i].compareTo(min) < 0) min = fractions[i];
        }
        return min;
    }

    /** 升序排序（直接修改传入数组并返回） */
    public static Fraction[] sort(Fraction[] fractions) {
        Arrays.sort(fractions);
        return fractions;
    }

    /** 总体方差 */
    public static Fraction variance(Fraction... fractions) {
        if (fractions.length < 2) throw new ArithmeticException("至少需要两个数");
        Fraction avg = average(fractions);
        Fraction sumSq = Fraction.ZERO();
        for (Fraction f : fractions) {
            Fraction diff = f.subtract(avg);
            sumSq = sumSq.add(diff.multiply(diff));
        }
        return sumSq.divide(new Fraction(BigInteger.valueOf(fractions.length)));
    }

    /** 总体标准差（返回 double 近似值） */
    public static double stdDev(Fraction... fractions) {
        return Math.sqrt(variance(fractions).toDouble());
    }

    /** 中位数 */
    public static Fraction median(Fraction... fractions) {
        if (fractions.length == 0) throw new ArithmeticException("空数组无中位数");
        Fraction[] sorted = fractions.clone();
        sort(sorted);
        int n = sorted.length;
        if (n % 2 == 1) {
            return sorted[n / 2];
        } else {
            Fraction lower = sorted[n / 2 - 1];
            Fraction upper = sorted[n / 2];
            return lower.add(upper).divide(new Fraction(BigInteger.valueOf(2)));
        }
    }

    /** 众数（返回出现次数最多的第一个值） */
    public static Fraction mode(Fraction... fractions) {
        if (fractions.length == 0) throw new ArithmeticException("空数组无众数");
        Map<Fraction, Integer> freq = new HashMap<>();
        for (Fraction f : fractions) {
            freq.put(f, freq.getOrDefault(f, 0) + 1);
        }
        Fraction mode = null;
        int maxCount = 0;
        for (Map.Entry<Fraction, Integer> entry : freq.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mode = entry.getKey();
            }
        }
        return mode;
    }

    /** 几何平均（返回 double） */
    public static double geometricMean(Fraction... fractions) {
        if (fractions.length == 0) throw new ArithmeticException("空数组");
        Fraction product = product(fractions);
        return Math.pow(product.toDouble(), 1.0 / fractions.length);
    }

    /** 调和平均 */
    public static Fraction harmonicMean(Fraction... fractions) {
        if (fractions.length == 0) throw new ArithmeticException("空数组");
        Fraction sumReciprocal = Fraction.ZERO();
        for (Fraction f : fractions) {
            if (f.getNumerator().signum() == 0) throw new ArithmeticException("调和平均包含零");
            sumReciprocal = sumReciprocal.add(f.reciprocal());
        }
        return new Fraction(BigInteger.valueOf(fractions.length)).divide(sumReciprocal);
    }

    /** 变异系数（标准差/平均值） */
    public static double coefficientOfVariation(Fraction... fractions) {
        Fraction avg = average(fractions);
        if (avg.signum() == 0) throw new ArithmeticException("平均值为零，无法计算变异系数");
        return stdDev(fractions) / avg.toDouble();
    }

    /** 生成随机正分数（分母不超过 maxDenominator） */
    public static Fraction randomPositive(int maxDenominator) {
        Random rand = new Random();
        int denominator = rand.nextInt(maxDenominator) + 1;
        int numerator = rand.nextInt(denominator * 10) + 1;
        return new Fraction(numerator, denominator);
    }

    /** 生成随机分数（可能为负） */
    public static Fraction random(int maxDenominator) {
        Fraction f = randomPositive(maxDenominator);
        Random rand = new Random();
        if (rand.nextBoolean()) f = f.negate();
        return f;
    }

    /** 将数组转换为分数数组 */
    public static Fraction[] toFractionArray(String... strs) {
        Fraction[] arr = new Fraction[strs.length];
        for (int i = 0; i < strs.length; i++) {
            arr[i] = Fraction.parseFraction(strs[i]);
        }
        return arr;
    }

    /** 对每个元素应用一元函数并返回新数组 */
    public static Fraction[] map(Fraction[] fractions, java.util.function.Function<Fraction,Fraction> func) {
        Fraction[] result = new Fraction[fractions.length];
        for (int i = 0; i < fractions.length; i++) {
            result[i] = func.apply(fractions[i]);
        }
        return result;
    }

    /** 计算两个数组的点积 */
    public static Fraction dotProduct(Fraction[] a, Fraction[] b) {
        if (a.length != b.length) throw new ArithmeticException("数组长度不一致");
        Fraction result = Fraction.ZERO();
        for (int i = 0; i < a.length; i++) {
            result = result.add(a[i].multiply(b[i]));
        }
        return result;
    }

    /** 计算欧几里得范数（各元素平方和的平方根） */
    public static double euclideanNorm(Fraction[] vec) {
        Fraction sumSq = Fraction.ZERO();
        for (Fraction f : vec) {
            sumSq = sumSq.add(f.multiply(f));
        }
        return Math.sqrt(sumSq.toDouble());
    }

    /** 将分数数组转换为大分数字符串 */
    public static String toString(Fraction[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(arr[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}