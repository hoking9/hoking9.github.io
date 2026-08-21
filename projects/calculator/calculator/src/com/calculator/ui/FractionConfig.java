package com.calculator.ui;

import java.io.*;
import java.util.Properties;

/**
 * 分数计算器配置管理。
 * 支持小数精度、窗口大小、主题颜色、自动保存历史等选项。
 * 配置存储在 fraction_calc.properties 文件中。
 */
public class FractionConfig {
    private static final String CONFIG_FILE = "fraction_calc.properties";
    private Properties props;

    public FractionConfig() {
        props = new Properties();
        load();
    }

    /**
     * 获取小数位数
     * @return 小数位数（默认 10）
     */
    public int getDecimalScale() {
        return Integer.parseInt(props.getProperty("decimalScale", "10"));
    }

    /**
     * 设置小数位数，范围 0~20
     * @param scale 小数位数
     */
    public void setDecimalScale(int scale) {
        if (scale < 0 || scale > 20) throw new IllegalArgumentException("小数位数范围0-20");
        props.setProperty("decimalScale", String.valueOf(scale));
        save();
    }

    /**
     * 是否自动保存历史
     * @return true 如果启用自动保存
     */
    public boolean isAutoSaveHistory() {
        return Boolean.parseBoolean(props.getProperty("autoSaveHistory", "true"));
    }

    /**
     * 设置是否自动保存历史
     * @param autoSave true 启用
     */
    public void setAutoSaveHistory(boolean autoSave) {
        props.setProperty("autoSaveHistory", String.valueOf(autoSave));
        save();
    }

    /**
     * 获取最近使用的历史导出路径
     * @return 路径字符串
     */
    public String getLastHistoryExportPath() {
        return props.getProperty("lastHistoryExportPath", "");
    }

    /**
     * 设置最近使用的历史导出路径
     * @param path 文件路径
     */
    public void setLastHistoryExportPath(String path) {
        props.setProperty("lastHistoryExportPath", path);
        save();
    }

    /**
     * 获取窗口宽度
     * @return 像素值（默认 820）
     */
    public int getWindowWidth() {
        return Integer.parseInt(props.getProperty("windowWidth", "820"));
    }

    /**
     * 设置窗口宽度
     * @param width 像素
     */
    public void setWindowWidth(int width) {
        props.setProperty("windowWidth", String.valueOf(width));
        save();
    }

    /**
     * 获取窗口高度
     * @return 像素值（默认 620）
     */
    public int getWindowHeight() {
        return Integer.parseInt(props.getProperty("windowHeight", "620"));
    }

    /**
     * 设置窗口高度
     * @param height 像素
     */
    public void setWindowHeight(int height) {
        props.setProperty("windowHeight", String.valueOf(height));
        save();
    }

    /**
     * 获取主题颜色
     * @return 颜色字符串（默认 "light"）
     */
    public String getTheme() {
        return props.getProperty("theme", "light");
    }

    /**
     * 设置主题颜色
     * @param theme "light" 或 "dark"
     */
    public void setTheme(String theme) {
        if (!theme.equals("light") && !theme.equals("dark"))
            throw new IllegalArgumentException("主题必须为 light 或 dark");
        props.setProperty("theme", theme);
        save();
    }

    /**
     * 获取所有属性（用于调试）
     * @return Properties 对象（克隆）
     */
    public Properties getProperties() {
        return (Properties) props.clone();
    }

    // ---------- 私有加载和保存 ----------

    private void load() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("加载配置失败: " + e.getMessage());
            }
        }
    }

    private void save() {
        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Fraction Calculator Configuration");
        } catch (IOException e) {
            System.err.println("保存配置失败: " + e.getMessage());
        }
    }

    /**
     * 重置所有配置为默认值
     */
    public void resetDefaults() {
        props.clear();
        props.setProperty("decimalScale", "10");
        props.setProperty("autoSaveHistory", "true");
        props.setProperty("lastHistoryExportPath", "");
        props.setProperty("windowWidth", "820");
        props.setProperty("windowHeight", "620");
        props.setProperty("theme", "light");
        save();
    }

    /**
     * 获取默认分数格式的小数位数（与 decimalScale 相同）
     * @return 小数位数
     */
    public int getDefaultFractionScale() {
        return getDecimalScale();
    }
}