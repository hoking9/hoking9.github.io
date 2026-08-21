package com.calculator.numbers;

/**
 * 分母为零异常，继承 ArithmeticException。
 * 当构造分数或设置分母时如果分母为零，则抛出此异常。
 */
public class DenominatorZeroException extends ArithmeticException {
    /** 默认构造方法 */
    public DenominatorZeroException() {
        super("分母不能为零");
    }

    /**
     * 带自定义消息的构造方法
     * @param message 异常描述
     */
    public DenominatorZeroException(String message) {
        super(message);
    }
}