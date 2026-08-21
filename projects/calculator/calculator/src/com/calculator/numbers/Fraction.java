package com.calculator.numbers;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Scanner;

/**
 * 分数类，继承 IntegerNumber，分子分母使用 BigInteger。
 * 自动化简，分母总为正。支持所有算术运算、精确小数、比较、取整、
 * 连分数展开、素因数分解等高级功能。
 */
public class Fraction extends IntegerNumber implements Comparable<Fraction> {

    /** 分母（始终为正） */
    private BigInteger denominator;

    // ===================== 构造函数 =====================

    /**
     * 构造分数，自动约分并确保分母为正。
     * @param numerator 分子
     * @param denominator 分母，不能为零
     * @throws DenominatorZeroException 如果 denominator == 0
     */
    public Fraction(BigInteger numerator, BigInteger denominator) {
        super(numerator);
        if (denominator.signum() == 0) throw new DenominatorZeroException("分母不能为零");
        this.denominator = denominator;
        reduce();
    }

    /**
     * 构造分数，可控制是否自动约分。
     * @param numerator 分子
     * @param denominator 分母
     * @param autoReduce 是否进行约分
     */
    public Fraction(BigInteger numerator, BigInteger denominator, boolean autoReduce) {
        super(numerator);
        if (denominator.signum() == 0) throw new DenominatorZeroException();
        this.denominator = denominator;
        if (autoReduce) reduce();
    }

    public Fraction(int numerator, int denominator) {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public Fraction(int numerator) {
        this(BigInteger.valueOf(numerator), BigInteger.ONE);
    }

    public Fraction(BigInteger numerator) {
        this(numerator, BigInteger.ONE);
    }

    public Fraction(IntegerNumber integerNumber) {
        this(integerNumber.getValue(), BigInteger.ONE);
    }

    /**
     * 从 long 构造整数分数
     * @param numerator 分子
     */
    public Fraction(long numerator) {
        this(BigInteger.valueOf(numerator));
    }

    // ===================== 常量工厂方法 =====================

    /** 返回 0 （分母为1） */
    public static Fraction ZERO() { return new Fraction(BigInteger.ZERO, BigInteger.ONE, false); }
    /** 返回 1 （分母为1） */
    public static Fraction ONE() { return new Fraction(BigInteger.ONE, BigInteger.ONE, false); }
    /** 返回 1/2 */
    public static Fraction HALF() { return new Fraction(BigInteger.ONE, BigInteger.valueOf(2), false); }
    /** 返回 -1 */
    public static Fraction MINUS_ONE() { return new Fraction(BigInteger.ONE.negate(), BigInteger.ONE, false); }
    /** 返回 2 */
    public static Fraction TWO() { return new Fraction(BigInteger.valueOf(2), BigInteger.ONE, false); }

    // ===================== 访问器 =====================

    /**
     * 获取分子
     * @return 分子
     */
    public BigInteger getNumerator() { return getValue(); }

    /**
     * 获取分母
     * @return 分母
     */
    public BigInteger getDenominator() { return denominator; }

    /**
     * 设置分子，然后进行约分
     * @param numerator 新分子
     */
    public void setNumerator(BigInteger numerator) {
        setValue(numerator);
        reduce();
    }

    /**
     * 设置分母，然后进行约分
     * @param denominator 新分母，不为零
     * @throws DenominatorZeroException 如果 denominator == 0
     */
    public void setDenominator(BigInteger denominator) {
        if (denominator.signum() == 0) throw new DenominatorZeroException();
        this.denominator = denominator;
        reduce();
    }

    // ===================== 私有化简 =====================

    /**
     * 化简分数，确保分母为正，分子分母互质
     * 直接使用父类 IntegerNumber 的静态 gcd 方法
     */
    private void reduce() {
        if (denominator.signum() < 0) {
            value = value.negate();
            denominator = denominator.negate();
        }
        if (value.signum() == 0) {
            denominator = BigInteger.ONE;
            return;
        }
        BigInteger g = IntegerNumber.gcd(value.abs(), denominator);
        value = value.divide(g);
        denominator = denominator.divide(g);
    }

    // ===================== 符号 =====================

    /**
     * 返回分数符号 -1, 0, 1
     * @return 符号
     */
    public int signum() { return value.signum(); }

    // ===================== 基本算术运算 =====================

    /**
     * 分数加法：通分后分子相加
     * @param other 加数
     * @return 最简分数结果
     */
    public Fraction add(Fraction other) {
        BigInteger l = lcm(this.denominator, other.denominator);
        BigInteger n1 = this.value.multiply(l.divide(this.denominator));
        BigInteger n2 = other.value.multiply(l.divide(other.denominator));
        return new Fraction(n1.add(n2), l);
    }

    /**
     * 分数减法
     * @param other 减数
     * @return 最简分数差
     */
    public Fraction subtract(Fraction other) {
        BigInteger l = lcm(this.denominator, other.denominator);
        BigInteger n1 = this.value.multiply(l.divide(this.denominator));
        BigInteger n2 = other.value.multiply(l.divide(other.denominator));
        return new Fraction(n1.subtract(n2), l);
    }

    /**
     * 分数乘法
     * @param other 乘数
     * @return 乘积
     */
    public Fraction multiply(Fraction other) {
        return new Fraction(this.value.multiply(other.value), this.denominator.multiply(other.denominator));
    }

    /**
     * 分数除法
     * @param other 除数
     * @return 商
     * @throws ArithmeticException 若除数为零
     */
    public Fraction divide(Fraction other) {
        if (other.value.signum() == 0) throw new ArithmeticException("除数不能为零");
        return new Fraction(this.value.multiply(other.denominator), this.denominator.multiply(other.value));
    }

    /**
     * 加法（int 版本）
     * @param num 整数
     * @return 和
     */
    public Fraction add(int num) { return add(new Fraction(num)); }

    /**
     * 减法（int 版本）
     * @param num 整数
     * @return 差
     */
    public Fraction subtract(int num) { return subtract(new Fraction(num)); }

    /**
     * 乘法（int 版本）
     * @param num 整数
     * @return 积
     */
    public Fraction multiply(int num) { return multiply(new Fraction(num)); }

    /**
     * 除法（int 版本）
     * @param num 除数（不能为零）
     * @return 商
     * @throws ArithmeticException 若 num == 0
     */
    public Fraction divide(int num) {
        if (num == 0) throw new ArithmeticException("除数不能为零");
        return divide(new Fraction(num));
    }

    /**
     * 加法（long 版本）
     */
    public Fraction add(long num) { return add(new Fraction(BigInteger.valueOf(num))); }

    /**
     * 减法（long 版本）
     */
    public Fraction subtract(long num) { return subtract(new Fraction(BigInteger.valueOf(num))); }

    /**
     * 乘法（long 版本）
     */
    public Fraction multiply(long num) { return multiply(new Fraction(BigInteger.valueOf(num))); }

    /**
     * 除法（long 版本）
     */
    public Fraction divide(long num) {
        if (num == 0) throw new ArithmeticException("除数不能为零");
        return divide(new Fraction(BigInteger.valueOf(num)));
    }

    // ===================== 单目运算 =====================

    /**
     * 倒数 (1/this)
     * @return 倒数
     * @throws ArithmeticException 如果 this == 0
     */
    public Fraction reciprocal() {
        if (value.signum() == 0) throw new ArithmeticException("零没有倒数");
        return new Fraction(denominator, value);
    }

    /**
     * 相反数 (-this)
     * @return 相反数
     */
    public Fraction negate() {
        return new Fraction(value.negate(), denominator);
    }

    /**
     * 绝对值
     * @return |this|
     */
    public Fraction abs() {
        return (value.signum() >= 0) ? this : negate();
    }

    /**
     * 整数次幂，支持负指数
     * @param exponent 指数
     * @return 乘方结果
     */
    public Fraction pow(int exponent) {
        if (exponent == 0) return ONE();
        BigInteger num = value;
        BigInteger den = denominator;
        if (exponent < 0) {
            BigInteger tmp = num; num = den; den = tmp;
            exponent = -exponent;
        }
        return new Fraction(num.pow(exponent), den.pow(exponent));
    }

    // ===================== 取整运算 =====================

    /**
     * 向下取整（不大于此分数的最大整数）
     * @return floor 值（分母为1）
     */
    public Fraction floor() {
        BigInteger[] divmod = value.divideAndRemainder(denominator);
        BigInteger quotient = divmod[0];
        if (value.signum() >= 0 || divmod[1].signum() == 0)
            return new Fraction(quotient, BigInteger.ONE, false);
        else
            return new Fraction(quotient.subtract(BigInteger.ONE), BigInteger.ONE, false);
    }

    /**
     * 向上取整（不小于此分数的最小整数）
     * @return ceil 值（分母为1）
     */
    public Fraction ceil() {
        BigInteger[] divmod = value.divideAndRemainder(denominator);
        BigInteger quotient = divmod[0];
        if (value.signum() <= 0 || divmod[1].signum() == 0)
            return new Fraction(quotient, BigInteger.ONE, false);
        else
            return new Fraction(quotient.add(BigInteger.ONE), BigInteger.ONE, false);
    }

    /**
     * 四舍五入
     * @return round 值（分母为1）
     */
    public Fraction round() {
        BigInteger[] divmod = value.divideAndRemainder(denominator);
        BigInteger remainder = divmod[1].abs().multiply(BigInteger.valueOf(2));
        int cmp = remainder.compareTo(denominator.abs());
        if (cmp < 0) return new Fraction(divmod[0], BigInteger.ONE, false);
        else if (cmp > 0) {
            BigInteger rounded = (value.signum() >= 0) ?
                    divmod[0].add(BigInteger.ONE) : divmod[0].subtract(BigInteger.ONE);
            return new Fraction(rounded, BigInteger.ONE, false);
        } else {
            if (value.signum() >= 0)
                return new Fraction(divmod[0].add(BigInteger.ONE), BigInteger.ONE, false);
            else
                return new Fraction(divmod[0], BigInteger.ONE, false);
        }
    }

    /**
     * 截断小数部分（向 0 方向取整）
     * @return 截断后的整数
     */
    public Fraction trunc() {
        return new Fraction(value.divide(denominator), BigInteger.ONE, false);
    }

    // ===================== 真分数判断与格式化 =====================

    /**
     * 判断是否为真分数（分子绝对值 < 分母绝对值）
     * @return true 如果是真分数
     */
    public boolean isProper() {
        return value.abs().compareTo(denominator.abs()) < 0;
    }

    /**
     * 判断分母是否为 1（即整数）
     * @return true 如果是整数
     */
    public boolean isInteger() {
        return denominator.equals(BigInteger.ONE);
    }

    /**
     * 返回标准字符串表示：分子/分母（分母不为1时）或整数
     * @return 字符串
     */
    @Override
    public String toString() {
        if (denominator.equals(BigInteger.ONE)) return value.toString();
        return value + "/" + denominator;
    }

    /**
     * 转换为带分数形式，例如 "1 3/4"
     * @return 带分数字符串
     */
    public String toMixedString() {
        if (denominator.equals(BigInteger.ONE)) return toString();
        BigInteger[] divmod = value.divideAndRemainder(denominator);
        BigInteger intPart = divmod[0];
        BigInteger rem = divmod[1].abs();
        StringBuilder sb = new StringBuilder();
        if (value.signum() < 0 && intPart.signum() == 0) sb.append("-");
        if (intPart.signum() != 0) {
            sb.append(intPart);
            if (rem.signum() != 0) sb.append(" ").append(rem).append("/").append(denominator.abs());
        } else {
            sb.append(rem).append("/").append(denominator.abs());
        }
        return sb.toString();
    }

    /**
     * 转换为带分数或真分数字符串
     * @return 格式化的字符串
     */
    public String toProperString() {
        if (isInteger()) return toString();
        if (isProper()) {
            boolean neg = value.signum() < 0;
            BigInteger absNum = value.abs();
            if (neg && value.compareTo(BigInteger.ZERO) < 0)
                return "-" + absNum + "/" + denominator.abs();
            else
                return absNum + "/" + denominator.abs();
        } else {
            return toMixedString();
        }
    }

    /**
     * 转换为指定小数位数的十进制字符串（四舍五入）
     * @param scale 小数位数
     * @return 十进制字符串
     */
    public String toDecimalString(int scale) {
        BigDecimal num = new BigDecimal(value), den = new BigDecimal(denominator);
        return num.divide(den, scale, RoundingMode.HALF_UP).toPlainString();
    }

    /**
     * 转换为 BigDecimal 并指定精度
     * @param scale 小数位数
     * @param mode 舍入模式
     * @return BigDecimal 对象
     */
    public BigDecimal toBigDecimal(int scale, RoundingMode mode) {
        return new BigDecimal(value).divide(new BigDecimal(denominator), scale, mode);
    }

    /**
     * 将分数转换为百分比字符串
     * @param scale 小数位数
     * @return 百分比字符串（如 "75.00%"）
     */
    public String toPercentString(int scale) {
        BigDecimal percent = new BigDecimal(value)
                .multiply(new BigDecimal("100"))
                .divide(new BigDecimal(denominator), scale, RoundingMode.HALF_UP);
        return percent.toPlainString() + "%";
    }

    /**
     * 将分数转换为双精度浮点数
     * @return double 近似值
     */
    public double toDouble() {
        return new BigDecimal(value).divide(new BigDecimal(denominator), 15, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 如果分母为1，返回分子 intValueExact()，否则抛异常
     * @return int 值
     */
    public int toInt() {
        if (!denominator.equals(BigInteger.ONE))
            throw new ArithmeticException("分数不是整数，无法转换为 int");
        return value.intValueExact();
    }

    /**
     * 平方根近似值（double）
     * @return sqrt(this) 的近似值
     */
    public double sqrtApprox() { return Math.sqrt(toDouble()); }

    /**
     * 转换为 BigDecimal 默认 15 位精度
     * @return BigDecimal
     */
    public BigDecimal toBigDecimal() {
        return toBigDecimal(15, RoundingMode.HALF_UP);
    }

    // ===================== 从 BigDecimal 构造 =====================

    /**
     * 从 BigDecimal 构造分数（保留所有小数精度）
     * @param dec 正或负的 BigDecimal
     * @return 等价分数
     */
    public static Fraction fromBigDecimal(BigDecimal dec) {
        String str = dec.toPlainString();
        int dotIdx = str.indexOf('.');
        if (dotIdx == -1) {
            return new Fraction(new BigInteger(str));
        } else {
            int decimals = str.length() - dotIdx - 1;
            BigInteger numerator = new BigInteger(str.replace(".", ""));
            BigInteger denominator = BigInteger.TEN.pow(decimals);
            return new Fraction(numerator, denominator);
        }
    }

    // ===================== 流操作模拟 =====================

    /**
     * 将分数写入输出流（模拟 C++ 流插入）
     * @param out 输出流
     */
    public void writeTo(java.io.PrintStream out) {
        out.print(this.toString());
    }

    /**
     * 从输入流读取分数（模拟 C++ 流提取）
     * @param scanner 扫描器
     * @return 解析的分数
     */
    public static Fraction readFrom(Scanner scanner) {
        return parseFraction(scanner.next());
    }

    /**
     * 从字符串解析分数，支持格式 "3/4", "5", "-7/2"
     * @param s 输入字符串
     * @return 分数对象
     */
    public static Fraction parseFraction(String s) {
        s = s.trim();
        int slashIndex = s.indexOf('/');
        if (slashIndex == -1) {
            return new Fraction(new BigInteger(s));
        }
        BigInteger num = new BigInteger(s.substring(0, slashIndex).trim());
        BigInteger den = new BigInteger(s.substring(slashIndex + 1).trim());
        return new Fraction(num, den);
    }

    // ===================== 比较与哈希 =====================

    /**
     * 判断相等（分子分母分别相等）
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Fraction f = (Fraction) obj;
        return value.equals(f.value) && denominator.equals(f.denominator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, denominator);
    }

    /**
     * 比较两个分数大小（交叉相乘）
     * @param other 另一个分数
     * @return -1, 0, 1
     */
    @Override
    public int compareTo(Fraction other) {
        BigInteger left = this.value.multiply(other.denominator);
        BigInteger right = other.value.multiply(this.denominator);
        return left.compareTo(right);
    }

    /**
     * 静态求和
     */
    public static Fraction sum(Fraction... fractions) {
        Fraction result = ZERO();
        for (Fraction f : fractions) result = result.add(f);
        return result;
    }

    // ===================== 高级数学运算（double 近似） =====================

    public double log() { return Math.log(toDouble()); }
    public double log10() { return Math.log10(toDouble()); }
    public double exp() { return Math.exp(toDouble()); }
    public double sin() { return Math.sin(toDouble()); }
    public double cos() { return Math.cos(toDouble()); }
    public double tan() { return Math.tan(toDouble()); }
    public double asin() { return Math.asin(toDouble()); }
    public double acos() { return Math.acos(toDouble()); }
    public double atan() { return Math.atan(toDouble()); }

    /** 双曲正弦 */
    public double sinh() { return Math.sinh(toDouble()); }
    /** 双曲余弦 */
    public double cosh() { return Math.cosh(toDouble()); }
    /** 双曲正切 */
    public double tanh() { return Math.tanh(toDouble()); }

    // ===================== 拷贝 =====================
    public Fraction copy() {
        return new Fraction(value, denominator, false);
    }

    // ===================== 其他工具方法 =====================

    /** 返回分数是否为非负（>=0） */
    public boolean isNonNegative() { return signum() >= 0; }

    /** 返回分数是否为非正（<=0） */
    public boolean isNonPositive() { return signum() <= 0; }

    /**
     * 返回分数的安全倒数（等价 reciprocal）
     */
    public Fraction safeReciprocal() {
        return reciprocal();
    }

    // ===================== 私有辅助：计算最小公倍数 =====================
    // 注意：这里使用了 IntegerNumber.gcd，因为 lcm 需要 gcd
    private static BigInteger lcm(BigInteger a, BigInteger b) {
        if (a.equals(BigInteger.ZERO) || b.equals(BigInteger.ZERO)) return BigInteger.ZERO;
        return a.divide(IntegerNumber.gcd(a, b)).multiply(b).abs();
    }
}