package com.calculator.ui;

import java.io.*;
import java.util.*;

/**
 * 历史记录管理器。
 *
 * 职责：
 * - 维护内存中的历史记录列表（List<String>）。
 * - 加载/保存历史记录到文件（默认文件 fraction_history.txt）。
 * - 提供添加、清空、导出、查询、统计等方法。
 *
 * 该管理器不是线程安全的，因为所有 GUI 调用都在事件调度线程进行。
 */
public class HistoryManager {

    /** 内存历史列表 */
    private final List<String> historyList;

    /** 历史记录文件 */
    private final File historyFile;

    /**
     * 构造管理器，并加载指定文件中的历史。
     * @param filePath 历史文件路径
     */
    public HistoryManager(String filePath) {
        this.historyList = new ArrayList<>();
        this.historyFile = new File(filePath);
        loadHistory();
    }

    /**
     * 从文件加载历史记录到列表。
     * 如果文件不存在则创建空文件。
     */
    private void loadHistory() {
        if (!historyFile.exists()) {
            try {
                // 确保父目录存在
                File parent = historyFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                historyFile.createNewFile();
            } catch (IOException e) {
                System.err.println("无法创建历史文件: " + e.getMessage());
            }
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(historyFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    historyList.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("读取历史文件失败: " + e.getMessage());
        }
    }

    /**
     * 添加一条记录（追加到内存列表和文件）。
     * @param record 记录字符串
     */
    public synchronized void addRecord(String record) {
        historyList.add(record);
        appendToFile(record);
    }

    /**
     * 向文件追加一条记录。
     * @param record 记录内容
     */
    private void appendToFile(String record) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(historyFile, true))) {
            writer.write(record);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("保存历史记录失败: " + e.getMessage());
        }
    }

    /**
     * 清除所有历史记录（内存和文件）。
     */
    public synchronized void clearHistory() {
        historyList.clear();
        try (PrintWriter pw = new PrintWriter(new FileWriter(historyFile))) {
            pw.print(""); // 清空文件内容
        } catch (IOException e) {
            System.err.println("清除历史记录失败: " + e.getMessage());
        }
    }

    /**
     * 返回历史记录的只读视图。
     * @return 历史记录列表（不可修改）
     */
    public List<String> getHistoryList() {
        return Collections.unmodifiableList(historyList);
    }

    /**
     * 获取历史记录总条数。
     * @return 记录条数
     */
    public int size() {
        return historyList.size();
    }

    /**
     * 检查历史记录是否为空。
     * @return true 如果空
     */
    public boolean isEmpty() {
        return historyList.isEmpty();
    }

    /**
     * 根据序号获取某条记录（序号从 0 开始）。
     * @param index 索引
     * @return 记录字符串
     * @throws IndexOutOfBoundsException 如果索引无效
     */
    public String getRecord(int index) {
        return historyList.get(index);
    }

    /**
     * 搜索含有指定关键词的历史记录。
     * @param keyword 关键词
     * @return 匹配的记录列表
     */
    public List<String> search(String keyword) {
        List<String> result = new ArrayList<>();
        for (String record : historyList) {
            if (record.contains(keyword)) {
                result.add(record);
            }
        }
        return result;
    }

    /**
     * 导出历史记录到指定文件。
     * @param file 目标文件
     * @return true 如果导出成功
     */
    public boolean exportHistory(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String record : historyList) {
                writer.write(record);
                writer.newLine();
            }
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("导出历史失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取历史文件路径。
     * @return 文件路径字符串
     */
    public String getFilePath() {
        return historyFile.getAbsolutePath();
    }

    /**
     * 重新加载历史记录（如果外部修改了文件）。
     */
    public synchronized void reload() {
        historyList.clear();
        loadHistory();
    }
}