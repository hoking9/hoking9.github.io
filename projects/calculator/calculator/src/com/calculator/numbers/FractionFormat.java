package com.calculator.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 分数格式化工具类，提供多种输出格式：
 * - 带分数、纯分数、小数、百分比
 * - 科学计数法、工程计数法
 * - 连分数展开
 */
public class FractionFormat {

    /** 全局默认小数位数 */
    public static int defaultScale = 10;

    /**
     * 将分数转换为带分数形式字符串
     * @param f 分数
     * @return 带分数字符串（如 "1 3/4"）
     */
    public static String toMixedString(Fraction f) {
        return f.toMixedString();
    }

    /**
     * 将分数转换为带分数或真分数形式
     * @param f 分数
     * @return 格式化字符串
     */
    public static String toProperString(Fraction f) {
        return f.toProperString();
    }

    /**
     * 转换为指定小数位数的十进制字符串
     * @param f 分数
     * @param scale 小数位数
     * @return 十进制字符串
     */
    public static String toDecimalString(Fraction f, int scale) {
        return f.toDecimalString(scale);
    }

    /**
     * 使用全局默认小数位数转换为十进制
     * @param f 分数
     * @return 十进制字符串
     */
    public static String toDecimalString(Fraction f) {
        return f.toDecimalString(defaultScale);
    }

    /**
     * 转换为百分比字符串
     * @param f 分数
     * @param scale 小数位数
     * @return 百分比字符串（如 "75.00%"）
     */
    public static String toPercentString(Fraction f, int scale) {
        BigDecimal percent = new BigDecimal(f.getNumerator())
                .multiply(new BigDecimal("100"))
                .divide(new BigDecimal(f.getDenominator()), scale, RoundingMode.HALF_UP);
        return percent.toPlainString() + "%";
    }

    /**
     * 转换为科学计数法字符串（如 "1.5E-1"）
     * @param f 分数
     * @param scale 小数位数
     * @return 科学计数法字符串
     */
    public static String toScientificString(Fraction f, int scale) {
        BigDecimal dec = f.toBigDecimal(scale + 5, RoundingMode.HALF_UP);
        String sci = String.format("%." + scale + "E", dec.doubleValue());
        return sci;
    }

    /**
     * 转换为工程计数法（指数为3的倍数）
     * @param f 分数
     * @param scale 小数位数
     * @return 工程计数法字符串
     */
    public static String toEngineeringString(Fraction f, int scale) {
        double val = f.toDouble();
        if (val == 0) return "0.0E0";
        int exp = (int) Math.floor(Math.log10(Math.abs(val)));
        int engExp = exp - exp % 3;
        double mantissa = val / Math.pow(10, engExp);
        return String.format("%." + scale + "fE%d", mantissa, engExp);
    }

    /**
     * 判断分数是否为真分数
     * @param f 分数
     * @return true 如果分子绝对值 < 分母绝对值
     */
    public static boolean isProper(Fraction f) {
        return f.isProper();
    }

    /**
     * 生成连分数展开的字符串表示
     * @param f 分数
     * @return 连分数字符串，例如 "[2; 1, 2]"
     */
    public static String toContinuedFractionString(Fraction f) {
        StringBuilder sb = new StringBuilder("[");
        java.math.BigInteger num = f.getNumerator();
        java.math.BigInteger den = f.getDenominator();
        boolean first = true;
        while (!den.equals(java.math.BigInteger.ZERO)) {
            java.math.BigInteger quotient = num.divide(den);
            if (first) {
                sb.append(quotient);
                first = false;
            } else {
                sb.append(", ").append(quotient);
            }
            java.math.BigInteger remainder = num.remainder(den);
            num = den;
            den = remainder;
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 生成分数的最简形式字符串（与 toString 一致）
     * @param f 分数
     * @return 字符串
     */
    public static String toSimpleString(Fraction f) {
        return f.toString();
    }
}