package com.calculator.main;

import com.calculator.ui.FractionCalculator;
import javax.swing.SwingUtilities;

/**
 * 程序入口类。
 * 在事件调度线程中启动计算器 GUI。
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FractionCalculator calc = new FractionCalculator();
            calc.setVisible(true);
        });
    }
}