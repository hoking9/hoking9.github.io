package com.calculator.ui;

import com.calculator.core.FractionExpressionParser;
import com.calculator.numbers.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.function.Function;

/**
 * 分数计算器主窗口，标准计算器形式。
 * 功能：
 * - 按钮输入与表达式计算
 * - 菜单驱动运算（加、减、乘、除、乘方、平方根、三角函数等）
 * - 历史记录管理（查看、清除、导出）
 * - 配置设置（小数位数、主题等）
 * - 分数数组统计工具
 * - 高级数学函数（sin/cos/tan/log/exp 等）
 * - 连分数显示
 * - 随机分数生成
 * - 复制结果到剪贴板等
 */
public class FractionCalculator extends JFrame implements ActionListener {

    // ==================== 界面组件 ====================
    private JTextField display;            // 主显示框
    private StringBuilder input;          // 当前输入缓冲区
    private boolean isResultDisplayed;    // 是否刚显示完结果
    private JLabel statusLabel;           // 底部状态栏

    // ==================== 历史记录 ====================
    private HistoryManager historyManager;
    private HistoryPanel historyPanel;

    // ==================== 配置 ====================
    private FractionConfig config;
    private int decimalScale;             // 当前小数的默认位数

    // ==================== 功能开关 ====================
    private boolean darkTheme = false;    // 是否深色主题（未在 UI 实现，占位）

    public FractionCalculator() {
        // 初始化核心对象
        input = new StringBuilder();
        isResultDisplayed = false;
        config = new FractionConfig();
        decimalScale = config.getDecimalScale();
        historyManager = new HistoryManager("fraction_history.txt");

        // 设置窗口属性
        setTitle("分数计算器(标准形式)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int width = config.getWindowWidth();
        int height = config.getWindowHeight();
        setSize(width, height);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        // 构建菜单栏
        initMenuBar();

        // 构建显示区域
        buildDisplay();

        // 构建主面板（按钮 + 历史面板）
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 按钮面板（6×4 网格）
        JPanel buttonPanel = buildButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // 历史记录面板
        historyPanel = new HistoryPanel(historyManager);
        mainPanel.add(historyPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        // 状态栏
        statusLabel = new JLabel("就绪 | 小数位数: " + decimalScale);
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);

        // 初始化显示
        historyPanel.refresh();
    }

    // ==================== 菜单栏构建 ====================
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // ---------- 操作菜单 ----------
        JMenu opMenu = new JMenu("操作");
        String[] basicOps = {
                "加法", "减法", "乘法", "除法", "乘方", "平方根",
                "化简", "倒数", "相反数", "绝对值", "比较", "精确小数",
                "连分数展开"
        };
        for (String op : basicOps) {
            JMenuItem item = new JMenuItem(op);
            item.addActionListener(this);
            opMenu.add(item);
        }
        opMenu.addSeparator();
        JMenuItem copyItem = new JMenuItem("复制结果");
        copyItem.addActionListener(this);
        opMenu.add(copyItem);
        opMenu.addSeparator();
        JMenuItem clearHistItem = new JMenuItem("清除历史");
        clearHistItem.addActionListener(this);
        opMenu.add(clearHistItem);
        JMenuItem exportHistItem = new JMenuItem("导出历史");
        exportHistItem.addActionListener(this);
        opMenu.add(exportHistItem);
        opMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(this);
        opMenu.add(exitItem);
        menuBar.add(opMenu);

        // ---------- 工具菜单 ----------
        JMenu toolMenu = new JMenu("工具");
        String[] arrayOps = {
                "数组求和", "数组求平均", "数组求最大值", "数组求最小值",
                "数组方差", "数组标准差", "数组中位数", "数组众数",
                "数组几何平均", "数组调和平均"
        };
        for (String tool : arrayOps) {
            JMenuItem item = new JMenuItem(tool);
            item.addActionListener(this);
            toolMenu.add(item);
        }
        toolMenu.addSeparator();
        JMenuItem randItem = new JMenuItem("生成随机分数");
        randItem.addActionListener(this);
        toolMenu.add(randItem);
        menuBar.add(toolMenu);

        // ---------- 高级数学菜单 ----------
        JMenu mathMenu = new JMenu("高级数学");
        String[] mathFuncs = {"sin", "cos", "tan", "asin", "acos", "atan",
                "sinh", "cosh", "tanh", "log", "log10", "exp"};
        for (String func : mathFuncs) {
            JMenuItem item = new JMenuItem(func);
            item.addActionListener(this);
            mathMenu.add(item);
        }
        menuBar.add(mathMenu);

        // ---------- 设置菜单 ----------
        JMenu settingsMenu = new JMenu("设置");
        JMenuItem decimalScaleItem = new JMenuItem("小数位数...");
        decimalScaleItem.addActionListener(this);
        settingsMenu.add(decimalScaleItem);
        JMenuItem themeItem = new JMenuItem("主题切换（暂未实现）");
        themeItem.addActionListener(this);
        settingsMenu.add(themeItem);
        // 重置配置
        JMenuItem resetConfigItem = new JMenuItem("恢复默认设置");
        resetConfigItem.addActionListener(this);
        settingsMenu.add(resetConfigItem);
        menuBar.add(settingsMenu);

        // ---------- 帮助菜单 ----------
        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(this);
        helpMenu.add(aboutItem);
        JMenuItem usageItem = new JMenuItem("使用说明");
        usageItem.addActionListener(this);
        helpMenu.add(usageItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    // ==================== 显示区域 ====================
    private void buildDisplay() {
        display = new JTextField();
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFont(new Font("微软雅黑", Font.PLAIN, 30));
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        add(display, BorderLayout.NORTH);
    }

    // ==================== 按钮面板 ====================
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 4, 5, 5));
        String[] buttons = {
                "C", "←", "%", "/",
                "7", "8", "9", "*",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", "(", ")", "^",
                "√", "D", "±", "="
        };
        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("微软雅黑", Font.PLAIN, 18));
            btn.addActionListener(this);
            if ("=+-".contains(text)) {
                btn.setForeground(Color.BLUE);
            } else if ("C←%/*-+()^√±".contains(text)) {
                btn.setForeground(Color.RED);
            }
            panel.add(btn);
        }
        return panel;
    }

    // ==================== 事件处理 ====================
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        // 菜单命令处理
        if (isMenuCommand(cmd)) {
            handleMenuCommand(cmd);
            return;
        }
        // 按钮命令处理
        try {
            processButtonCommand(cmd);
        } catch (ArithmeticException ex) {
            showError("计算错误: " + ex.getMessage());
        } catch (Exception ex) {
            showError("错误: " + ex.getMessage());
        }
    }

    /**
     * 判断命令是否为菜单项命令
     */
    private boolean isMenuCommand(String cmd) {
        // 简单判断：不在按钮集合内的都可能是菜单命令
        // 我们列出所有按钮字符串，若都不匹配则为菜单命令
        String[] buttonCmds = {
                "C", "←", "%", "/",
                "7", "8", "9", "*",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", "(", ")", "^",
                "√", "D", "±", "="
        };
        for (String bc : buttonCmds) {
            if (bc.equals(cmd)) return false;
        }
        return true;
    }

    /**
     * 处理按钮点击
     */
    private void processButtonCommand(String cmd) throws Exception {
        if (isResultDisplayed && cmd.matches("[0-9]")) {
            input.setLength(0);
            isResultDisplayed = false;
        }
        if (cmd.matches("[0-9+\\-*/.()^]")) {
            input.append(cmd);
            display.setText(input.toString());
        } else if (cmd.equals("C")) {
            input.setLength(0);
            display.setText("");
            isResultDisplayed = false;
        } else if (cmd.equals("←")) {
            if (input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
            }
            display.setText(input.toString());
        } else if (cmd.equals("=")) {
            calculateExpression();
        } else if (cmd.equals("±")) {
            toggleSign();
        } else if (cmd.equals("%")) {
            applyPercent();
        } else if (cmd.equals("√")) {
            applySqrt();
        } else if (cmd.equals("D")) {
            convertToDecimal();
        } else if (cmd.equals("Red")) {
            reduceFraction();
        }
    }

    // ---------- 按钮功能实现 ----------

    /**
     * 计算当前输入表达式
     */
    private void calculateExpression() throws Exception {
        String expr = input.toString().trim();
        if (expr.isEmpty()) return;
        Fraction result = FractionExpressionParser.parse(expr);
        String resultStr = result.toString();
        display.setText(resultStr);
        input = new StringBuilder(resultStr);
        isResultDisplayed = true;
        historyManager.addRecord(expr + " = " + resultStr);
        historyPanel.refresh();
    }

    /**
     * 切换当前输入的正负号
     */
    private void toggleSign() throws Exception {
        String cur = input.toString().trim();
        if (cur.isEmpty()) {
            input.append("-");
            display.setText(input.toString());
            return;
        }
        try {
            Fraction f = Fraction.parseFraction(cur);
            f = f.negate();
            String newStr = f.toString();
            input = new StringBuilder(newStr);
            display.setText(newStr);
            isResultDisplayed = true;
        } catch (Exception ex) {
            // 如果不是合法分数，直接切换第一个字符的负号
            if (cur.startsWith("-")) {
                input.deleteCharAt(0);
            } else {
                input.insert(0, "-");
            }
            display.setText(input.toString());
        }
    }

    /**
     * 对当前输入取百分号（×1/100）
     */
    private void applyPercent() throws Exception {
        String cur = input.toString().trim();
        if (cur.isEmpty()) return;
        Fraction f = Fraction.parseFraction(cur);
        Fraction result = f.multiply(new Fraction(1, 100));
        String resultStr = result.toString();
        display.setText(resultStr);
        input = new StringBuilder(resultStr);
        isResultDisplayed = true;
        historyManager.addRecord("(" + cur + ")% = " + resultStr);
        historyPanel.refresh();
    }

    /**
     * 对当前输入开平方
     */
    private void applySqrt() throws Exception {
        String cur = input.toString().trim();
        if (cur.isEmpty()) return;
        Fraction f = Fraction.parseFraction(cur);
        double val = f.toDouble();
        if (val < 0) {
            showError("负数不能开平方");
            return;
        }
        double sqrt = Math.sqrt(val);
        String resultStr = String.format("%." + decimalScale + "f", sqrt);
        display.setText(resultStr);
        input = new StringBuilder(resultStr);
        isResultDisplayed = true;
        historyManager.addRecord("√(" + cur + ") = " + resultStr);
        historyPanel.refresh();
    }

    /**
     * 将当前输入转换为指定精度的十进制小数
     */
    private void convertToDecimal() throws Exception {
        String cur = input.toString().trim();
        if (cur.isEmpty()) return;
        Fraction f = Fraction.parseFraction(cur);
        String decimal = f.toDecimalString(decimalScale);
        String record = cur + " ≈ " + decimal;
        display.setText(decimal);
        input = new StringBuilder(decimal);
        isResultDisplayed = true;
        historyManager.addRecord(record);
        historyPanel.refresh();
    }

    /**
     * 化简当前分数（显示最简形式）
     */
    private void reduceFraction() throws Exception {
        String cur = input.toString().trim();
        if (cur.isEmpty()) return;
        Fraction f = Fraction.parseFraction(cur);
        String resultStr = f.toString();
        display.setText(resultStr);
        input = new StringBuilder(resultStr);
        isResultDisplayed = true;
        historyManager.addRecord("化简: " + cur + " = " + resultStr);
        historyPanel.refresh();
    }

    // ---------- 错误提示 ----------
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
        input.setLength(0);
        display.setText("");
        isResultDisplayed = false;
    }

    // ==================== 菜单命令处理 ====================
    private void handleMenuCommand(String cmd) {
        switch (cmd) {
            // 基本运算
            case "加法": binaryOp("加", (a,b) -> a.add(b)); break;
            case "减法": binaryOp("减", (a,b) -> a.subtract(b)); break;
            case "乘法": binaryOp("乘", (a,b) -> a.multiply(b)); break;
            case "除法": binaryOp("除", (a,b) -> a.divide(b)); break;
            case "乘方": binaryOp("乘方", (a,b) -> {
                if (!b.getDenominator().equals(java.math.BigInteger.ONE))
                    throw new ArithmeticException("指数必须是整数");
                return a.pow(b.toInt());
            }); break;
            case "平方根": menuSqrt(); break;
            case "化简": unaryOp("化简", f -> f); break;
            case "倒数": unaryOp("倒数", f -> f.reciprocal()); break;
            case "相反数": unaryOp("相反数", f -> f.negate()); break;
            case "绝对值": unaryOp("绝对值", f -> f.abs()); break;
            case "比较": menuCompare(); break;
            case "精确小数": menuDecimal(); break;
            case "连分数展开": menuContinuedFraction(); break;
            case "复制结果": copyResult(); break;

            // 历史相关
            case "清除历史":
                historyManager.clearHistory();
                historyPanel.refresh();
                JOptionPane.showMessageDialog(this, "历史已清除");
                break;
            case "导出历史": exportHistory(); break;

            // 工具菜单
            case "数组求和": arrOp("求和", FractionUtil::sum); break;
            case "数组求平均": arrOp("平均", FractionUtil::average); break;
            case "数组求最大值": arrOp("最大值", FractionUtil::max); break;
            case "数组求最小值": arrOp("最小值", FractionUtil::min); break;
            case "数组方差": arrOp("方差", fractions -> FractionUtil.variance(fractions)); break;
            case "数组标准差": {
                String inputStr = JOptionPane.showInputDialog(this, "请输入分数序列（空格分隔）：");
                if (inputStr == null) return;
                try {
                    String[] parts = inputStr.trim().split("\\s+");
                    Fraction[] arr = new Fraction[parts.length];
                    for (int i = 0; i < parts.length; i++) arr[i] = Fraction.parseFraction(parts[i]);
                    double std = FractionUtil.stdDev(arr);
                    String result = "标准差 = " + String.format("%.6f", std);
                    JOptionPane.showMessageDialog(this, result);
                    display.setText(result);
                    historyManager.addRecord("数组标准差: " + result);
                    historyPanel.refresh();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "输入错误: " + e.getMessage());
                }
                break;
            }
            case "数组中位数": arrOp("中位数", FractionUtil::median); break;
            case "数组众数": arrOp("众数", fractions -> FractionUtil.mode(fractions)); break;
            case "数组几何平均": {
                // 几何平均返回 double
                String inputStr = JOptionPane.showInputDialog(this, "请输入分数序列（空格分隔）：");
                if (inputStr == null) return;
                try {
                    String[] parts = inputStr.trim().split("\\s+");
                    Fraction[] arr = new Fraction[parts.length];
                    for (int i = 0; i < parts.length; i++) arr[i] = Fraction.parseFraction(parts[i]);
                    double gm = FractionUtil.geometricMean(arr);
                    String result = "几何平均 = " + String.format("%.6f", gm);
                    JOptionPane.showMessageDialog(this, result);
                    display.setText(result);
                    historyManager.addRecord("数组几何平均: " + result);
                    historyPanel.refresh();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "错误: " + e.getMessage());
                }
                break;
            }
            case "数组调和平均": arrOp("调和平均", FractionUtil::harmonicMean); break;

            case "生成随机分数": generateRandomFraction(); break;

            // 高级数学函数
            case "sin": case "cos": case "tan": case "asin": case "acos": case "atan":
            case "sinh": case "cosh": case "tanh": case "log": case "log10": case "exp":
                advancedMath(cmd);
                break;

            // 设置
            case "小数位数...": setDecimalScale(); break;
            case "主题切换（暂未实现）":
                JOptionPane.showMessageDialog(this, "主题切换功能暂未开放，敬请期待。");
                break;
            case "恢复默认设置":
                config.resetDefaults();
                decimalScale = config.getDecimalScale();
                updateStatusBar();
                JOptionPane.showMessageDialog(this, "已恢复默认设置。");
                break;

            // 帮助
            case "关于": showAbout(); break;
            case "使用说明": showHelp(); break;

            // 退出
            case "退出": System.exit(0); break;

            default:
                JOptionPane.showMessageDialog(this, "未知命令: " + cmd);
        }
    }

    // ---------- 菜单辅助方法 ----------

    /**
     * 更新状态栏显示
     */
    private void updateStatusBar() {
        statusLabel.setText("就绪 | 小数位数: " + decimalScale);
    }

    /**
     * 复制当前显示框内容到剪贴板
     */
    private void copyResult() {
        String text = display.getText();
        if (text.isEmpty()) return;
        java.awt.Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new java.awt.datatransfer.StringSelection(text), null);
        JOptionPane.showMessageDialog(this, "已复制到剪贴板: " + text);
    }

    /**
     * 导出历史记录
     */
    private void exportHistory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("history_export.txt"));
        int ret = chooser.showSaveDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (historyManager.exportHistory(file)) {
                JOptionPane.showMessageDialog(this, "历史已导出至:\n" + file.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(this, "导出失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 生成随机分数
     */
    private void generateRandomFraction() {
        String maxDen = JOptionPane.showInputDialog(this, "请输入最大分母（例如 100）:");
        if (maxDen == null) return;
        try {
            int md = Integer.parseInt(maxDen.trim());
            if (md <= 0) throw new NumberFormatException();
            Fraction rand = FractionUtil.randomPositive(md);
            String msg = "随机分数: " + rand + " ≈ " + rand.toDecimalString(decimalScale);
            JOptionPane.showMessageDialog(this, msg);
            display.setText(msg);
            historyManager.addRecord(msg);
            historyPanel.refresh();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入正整数");
        }
    }

    /**
     * 设置小数位数
     */
    private void setDecimalScale() {
        String input = JOptionPane.showInputDialog(this,
                "请输入小数显示位数（0 ~ 20）", String.valueOf(decimalScale));
        if (input == null) return;
        try {
            int scale = Integer.parseInt(input.trim());
            if (scale >= 0 && scale <= 20) {
                decimalScale = scale;
                config.setDecimalScale(scale);
                updateStatusBar();
                JOptionPane.showMessageDialog(this, "小数位数已设为 " + scale);
            } else {
                JOptionPane.showMessageDialog(this, "请输入 0 至 20 之间的整数！");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "输入无效，必须是整数。");
        }
    }

    /**
     * 一元操作（弹出输入对话框，调用一元函数）
     */
    private void unaryOp(String opName, Function<Fraction, Fraction> op) {
        String s = JOptionPane.showInputDialog(this, "输入分数（如 3/4 或 5）:");
        if (s == null) return;
        try {
            Fraction f = Fraction.parseFraction(s);
            Fraction result = op.apply(f);
            String expr = opName + "(" + f + ") = " + result;
            JOptionPane.showMessageDialog(this, expr);
            display.setText(expr);
            historyManager.addRecord(expr);
            historyPanel.refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "错误: " + e.getMessage());
        }
    }

    /**
     * 二元操作
     */
    @FunctionalInterface
    interface BinaryFractionOp {
        Fraction apply(Fraction a, Fraction b) throws Exception;
    }

    private void binaryOp(String opName, BinaryFractionOp op) {
        String s1 = JOptionPane.showInputDialog(this, "输入第一个分数（如 3/4 或 5）:");
        if (s1 == null) return;
        String s2 = JOptionPane.showInputDialog(this, "输入第二个分数:");
        if (s2 == null) return;
        try {
            Fraction f1 = Fraction.parseFraction(s1);
            Fraction f2 = Fraction.parseFraction(s2);
            Fraction result = op.apply(f1, f2);
            String expr = f1 + " " + opName + " " + f2 + " = " + result;
            JOptionPane.showMessageDialog(this, expr);
            display.setText(expr);
            historyManager.addRecord(expr);
            historyPanel.refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "计算错误: " + e.getMessage());
        }
    }

    /**
     * 数组操作（输入空格分隔的分数序列，计算结果 Fraction）
     */
    private void arrOp(String opName, java.util.function.Function<Fraction[], Fraction> func) {
        String inputStr = JOptionPane.showInputDialog(this,
                "请输入分数序列（如 1/2 3/4 2），空格分隔：");
        if (inputStr == null) return;
        try {
            String[] parts = inputStr.trim().split("\\s+");
            Fraction[] arr = new Fraction[parts.length];
            for (int i = 0; i < parts.length; i++) {
                arr[i] = Fraction.parseFraction(parts[i]);
            }
            Fraction result = func.apply(arr);
            String expr = "数组" + opName + " = " + result;
            JOptionPane.showMessageDialog(this, expr);
            display.setText(expr);
            historyManager.addRecord(expr);
            historyPanel.refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "错误: " + e.getMessage());
        }
    }

    /**
     * 高级数学函数（sin/cos/tan...）
     */
    private void advancedMath(String func) {
        String inputStr = JOptionPane.showInputDialog(this, "输入分数或角度（弧度制）:");
        if (inputStr == null) return;
        try {
            Fraction f = Fraction.parseFraction(inputStr);
            double result;
            switch (func) {
                case "sin": result = f.sin(); break;
                case "cos": result = f.cos(); break;
                case "tan": result = f.tan(); break;
                case "asin": result = Math.asin(f.toDouble()); break;
                case "acos": result = Math.acos(f.toDouble()); break;
                case "atan": result = Math.atan(f.toDouble()); break;
                case "sinh": result = Math.sinh(f.toDouble()); break;
                case "cosh": result = Math.cosh(f.toDouble()); break;
                case "tanh": result = Math.tanh(f.toDouble()); break;
                case "log": {
                    if (f.toDouble() <= 0) throw new ArithmeticException("对数真数必须为正");
                    result = f.log();
                    break;
                }
                case "log10": {
                    if (f.toDouble() <= 0) throw new ArithmeticException("对数真数必须为正");
                    result = f.log10();
                    break;
                }
                case "exp": result = f.exp(); break;
                default: return;
            }
            String resStr = func + "(" + f + ") = " + String.format("%." + decimalScale + "f", result);
            JOptionPane.showMessageDialog(this, resStr);
            display.setText(resStr);
            historyManager.addRecord(resStr);
            historyPanel.refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "错误: " + e.getMessage());
        }
    }

    /**
     * 菜单平方根
     */
    private void menuSqrt() {
        String s = JOptionPane.showInputDialog(this, "输入分数（如 9/4）:");
        if (s == null) return;
        try {
            Fraction f = Fraction.parseFraction(s);
            double val = f.toDouble();
            if (val < 0) throw new ArithmeticException("负数不能开平方");
            double sqrt = Math.sqrt(val);
            String resultStr = String.format("%." + decimalScale + "f", sqrt);
            String expr = "√(" + f + ") = " + resultStr;
            JOptionPane.showMessageDialog(this, expr);
            display.setText(expr);
            historyManager.addRecord(expr);
            historyPanel.refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "错误: " + e.getMessage());
        }
    }

    /**
     * 菜单比较
     */
    private void menuCompare() {
        String s1 = JOptionPane.showInputDialog(this, "输入第一个分数（如 3/4 或 5）:");
        if (s1 == null) return;
        String s2 = JOptionPane.showInputDialog(this, "输入第二个分数:");
        if (s2 == null) return;
        try {
            Fraction f1 = Fraction.parseFraction(s1);
            Fraction f2 = Fraction.parseFraction(s2);
            int cmp = f1.compareTo(f2);
            String rel;
            if (cmp < 0) rel = " < ";
            else if (cmp > 0) rel = " > ";
            else rel = " = ";
            String result = f1 + rel + f2;
            JOptionPane.showMessageDialog(this, result);
            display.setText(result);
            historyManager.addRecord("比较: " + result);
            historyPanel.refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "输入错误: " + e.getMessage());
        }
    }

    /**
     * 菜单精确小数
     */
    private void menuDecimal() {
        String s = JOptionPane.showInputDialog(this, "输入分数（如 355/113）:");
        if (s == null) return;
        try {
            Fraction f = Fraction.parseFraction(s);
            String decimal = f.toDecimalString(decimalScale);
            String result = f + " ≈ " + decimal;
            JOptionPane.showMessageDialog(this, result);
            display.setText(result);
            historyManager.addRecord(result);
            historyPanel.refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "错误: " + e.getMessage());
        }
    }

    /**
     * 连分数展开
     */
    private void menuContinuedFraction() {
        String s = JOptionPane.showInputDialog(this, "输入分数（如 7/5）:");
        if (s == null) return;
        try {
            Fraction f = Fraction.parseFraction(s);
            String cont = FractionFormat.toContinuedFractionString(f);
            String result = "连分数: " + f + " = " + cont;
            JOptionPane.showMessageDialog(this, result);
            display.setText(result);
            historyManager.addRecord(result);
            historyPanel.refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "错误: " + e.getMessage());
        }
    }

    /**
     * 关于对话框
     */
    private void showAbout() {
        String info = "分数计算器 \n\n" +
                "本软件是基于 Java 的分数计算器，提供丰富的分数运算功能：\n" +
                "- 四则运算、乘方、平方根\n" +
                "- 化简、倒数、相反数、绝对值、比较\n" +
                "- 精确小数、百分号、连分数展开\n" +
                "- 表达式解析（支持括号、函数）\n" +
                "- 分数数组统计（求和、平均、方差、中位数等）\n" +
                "- 高级数学函数（三角函数、对数、指数等）\n" +
                "- 历史记录管理（保存、导出、清除）\n" +
                "- 可配置小数位数\n" +
                "\n作者: 优化小组\n" +
                "版本: 3.0 (2025)";
        JOptionPane.showMessageDialog(this, info, "关于", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 使用说明
     */
    private void showHelp() {
        String help = "分数计算器 使用说明\n\n" +
                "【表达式输入】\n" +
                "在文本框中直接输入数学表达式，支持以下操作：\n" +
                "  + 加法   - 减法   * 乘法   / 除法   ^ 乘方\n" +
                "  ( ) 括号  abs( ) neg( ) sqrt( ) floor( ) ceil( ) round( ) 等函数\n" +
                "示例： (1/2 + 3/4) * 2^3  或  sqrt(9/4) + abs(-3/4)\n\n" +
                "【按钮说明】\n" +
                "  C: 清空   ←: 退格   %: 百分号   /: 除法或分数分隔\n" +
                "  √: 开平方   D: 转小数   ±: 切换正负    Red: 化简   =: 求值\n\n" +
                "【菜单功能】\n" +
                "  操作: 提供所有基本运算、小数、连分数、复制结果等\n" +
                "  工具: 分数数组的统计计算（求和、平均、方差等）以及随机分数生成\n" +
                "  高级数学: 三角函数、双曲函数、对数、指数\n" +
                "  设置: 更改小数显示位数\n\n" +
                " 所有计算结果自动保存到历史记录，可通过“清除历史”或“导出历史”管理。";
        JOptionPane.showMessageDialog(this, help, "使用说明", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== 主方法（程序入口） ====================
    public static void main(String[] args) {
        // 设置系统 LookAndFeel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // 忽略外观设置异常
        }
        SwingUtilities.invokeLater(() -> {
            FractionCalculator calculator = new FractionCalculator();
            calculator.setVisible(true);
        });
    }
}