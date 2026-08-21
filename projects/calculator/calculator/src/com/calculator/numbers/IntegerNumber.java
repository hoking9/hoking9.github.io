package com.calculator.numbers;

import java.math.BigInteger;
import java.util.Random;

/**
 * 整数基类，使用 BigInteger 实现无限精度。
 * 提供基本的整数值操作、进制转换、静态阶乘、随机生成等功能。
 */
public class IntegerNumber {

    /** 内部存储的 BigInteger 值 */
    protected BigInteger value;

    /**
     * 构造方法，接受 BigInteger
     * @param value 整数值
     */
    public IntegerNumber(BigInteger value) {
        this.value = value;
    }

    /**
     * 构造方法，接受 int
     * @param value 整数值
     */
    public IntegerNumber(int value) {
        this(BigInteger.valueOf(value));
    }

    /**
     * 构造方法，接受 long
     * @param value 整数值
     */
    public IntegerNumber(long value) {
        this(BigInteger.valueOf(value));
    }

    /**
     * 返回内部 BigInteger 值
     * @return 当前值
     */
    public BigInteger getValue() {
        return value;
    }

    /**
     * 设置内部值
     * @param value 新的 BigInteger 值
     */
    public void setValue(BigInteger value) {
        this.value = value;
    }

    /**
     * 设置内部值（int 版本）
     * @param value 新的 int 值
     */
    public void setValue(int value) {
        this.value = BigInteger.valueOf(value);
    }

    /**
     * 判断值是否为正数（严格大于0）
     * @return true 如果 value > 0
     */
    public boolean isPositive() {
        return value.signum() > 0;
    }

    /**
     * 判断值是否为负数（严格小于0）
     * @return true 如果 value < 0
     */
    public boolean isNegative() {
        return value.signum() < 0;
    }

    /**
     * 判断值是否为零
     * @return true 如果 value == 0
     */
    public boolean isZero() {
        return value.signum() == 0;
    }

    /**
     * 返回符号：-1、0、1
     * @return 符号值
     */
    public int signum() {
        return value.signum();
    }

    /**
     * 返回相反数的新对象
     * @return 值为 -value 的新 IntegerNumber
     */
    public IntegerNumber negate() {
        return new IntegerNumber(value.negate());
    }

    /**
     * 返回绝对值的新对象
     * @return 值为 |value| 的新 IntegerNumber
     */
    public IntegerNumber abs() {
        return new IntegerNumber(value.abs());
    }

    /**
     * 判断两个整数对象是否相等（值相等）
     * @param obj 要比较的对象
     * @return true 如果值相等
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IntegerNumber that = (IntegerNumber) obj;
        return value.equals(that.value);
    }

    /**
     * 哈希码
     * @return 值的哈希码
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * 返回整数的十进制字符串表示
     * @return 字符串
     */
    @Override
    public String toString() {
        return value.toString();
    }

    // ===================== 进制转换 =====================

    /**
     * 转换为二进制字符串
     * @return 无前导零的二进制字符串
     */
    public String toBinaryString() {
        return value.toString(2);
    }

    /**
     * 转换为十六进制字符串（大写）
     * @return 十六进制字符串
     */
    public String toHexString() {
        return value.toString(16).toUpperCase();
    }

    /**
     * 转换为八进制字符串
     * @return 八进制字符串
     */
    public String toOctalString() {
        return value.toString(8);
    }

    /**
     * 转换为二进制补码字符串（仅当值为负数时才有实际意义）
     * @return 二进制补码字符串（与 BigInteger 内部表示相关）
     */
    public String toTwoComplementString() {
        if (value.signum() >= 0) {
            return "0" + toBinaryString();
        } else {
            BigInteger twosComplement = value.and(BigInteger.ONE.shiftLeft(value.bitLength()).subtract(BigInteger.ONE));
            return twosComplement.toString(2);
        }
    }

    // ===================== 静态工具方法 =====================

    /**
     * 计算最大公约数
     * @param a 第一个数
     * @param b 第二个数
     * @return 最大公约数（正值）
     */
    public static BigInteger gcd(BigInteger a, BigInteger b) {
        a = a.abs();
        b = b.abs();
        if (a.equals(BigInteger.ZERO)) return b;
        if (b.equals(BigInteger.ZERO)) return a;
        return a.gcd(b);
    }

    /**
     * 计算 n 的阶乘
     * @param n 非负整数
     * @return n! （BigInteger）
     * @throws ArithmeticException 如果 n 为负数
     */
    public static BigInteger factorial(int n) {
        if (n < 0) throw new ArithmeticException("负数的阶乘未定义");
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    /**
     * 判断一个 BigInteger 是否为质数
     * @param num 要检查的数
     * @return true 如果 num 很可能是质数
     */
    public static boolean isPrime(BigInteger num) {
        if (num.compareTo(BigInteger.ONE) <= 0) return false;
        if (num.equals(BigInteger.TWO)) return true;
        if (num.mod(BigInteger.TWO).equals(BigInteger.ZERO)) return false;
        // 使用 Miller-Rabin 测试
        return num.isProbablePrime(100);
    }

    /**
     * 生成随机 BigInteger（小于 max）
     * @param max 上限（不包含）
     * @return 随机 BigInteger
     */
    public static BigInteger randomBigInteger(BigInteger max) {
        Random rand = new Random();
        BigInteger result;
        do {
            result = new BigInteger(max.bitLength(), rand);
        } while (result.compareTo(max) >= 0);
        return result;
    }

    /**
     * 工厂方法，从 long 创建
     * @param value 长整数值
     * @return IntegerNumber 实例
     */
    public static IntegerNumber valueOf(long value) {
        return new IntegerNumber(value);
    }

    /**
     * 拷贝当前对象（由于 BigInteger 不可变，可返回新对象）
     * @return 值相同的 IntegerNumber 对象
     */
    public IntegerNumber copy() {
        return new IntegerNumber(this.value);
    }

    /**
     * 返回 (this + other) 的和
     * @param other 加数
     * @return 和
     */
    public IntegerNumber add(IntegerNumber other) {
        return new IntegerNumber(this.value.add(other.value));
    }

    /**
     * 返回 (this - other) 的差
     * @param other 减数
     * @return 差
     */
    public IntegerNumber subtract(IntegerNumber other) {
        return new IntegerNumber(this.value.subtract(other.value));
    }

    /**
     * 返回 (this * other) 的积
     * @param other 乘数
     * @return 积
     */
    public IntegerNumber multiply(IntegerNumber other) {
        return new IntegerNumber(this.value.multiply(other.value));
    }

    /**
     * 返回 (this / other) 的商（整数除法）
     * @param other 除数
     * @return 商
     * @throws ArithmeticException 如果除数为0
     */
    public IntegerNumber divide(IntegerNumber other) {
        if (other.value.equals(BigInteger.ZERO)) throw new ArithmeticException("除数不能为零");
        return new IntegerNumber(this.value.divide(other.value));
    }

    /**
     * 返回 this % other 的余数
     * @param other 除数
     * @return 余数
     */
    public IntegerNumber mod(IntegerNumber other) {
        if (other.value.equals(BigInteger.ZERO)) throw new ArithmeticException("除数不能为零");
        return new IntegerNumber(this.value.remainder(other.value));
    }

    /**
     * 返回 (this ^ exponent) 的幂
     * @param exponent 指数
     * @return 幂
     */
    public IntegerNumber pow(int exponent) {
        return new IntegerNumber(this.value.pow(exponent));
    }

    /**
     * 比较 this 与 other 的大小
     * @param other 另一个整数
     * @return -1, 0, 1
     */
    public int compareTo(IntegerNumber other) {
        return this.value.compareTo(other.value);
    }
}