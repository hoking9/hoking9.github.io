package com.calculator.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * 历史记录显示面板。
 *
 * 布局：
 * - 顶部标题 "历史记录"
 * - 中间可滚动的 JTextArea
 * - 底部功能按钮（清除、导出、刷新）
 */
public class HistoryPanel extends JPanel {

    private JTextArea historyArea;
    private HistoryManager historyManager;
    private JLabel statusLabel;  // 显示记录条数

    /**
     * 构造历史记录面板。
     * @param manager 历史记录管理器
     */
    public HistoryPanel(HistoryManager manager) {
        this.historyManager = manager;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("历史记录"));

        // 状态标签（显示记录条数）
        statusLabel = new JLabel("共 0 条记录");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        add(statusLabel, BorderLayout.NORTH);

        // 历史显示区域
        historyArea = new JTextArea(15, 25);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(historyArea);
        add(scrollPane, BorderLayout.CENTER);

        // 底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton clearBtn = new JButton("清除历史");
        clearBtn.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this,
                    "确定要清除所有历史记录吗？",
                    "确认清除", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                historyManager.clearHistory();
                refresh();
            }
        });
        buttonPanel.add(clearBtn);

        JButton exportBtn = new JButton("导出历史");
        exportBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("history_export.txt"));
            int ret = fileChooser.showSaveDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (historyManager.exportHistory(file)) {
                    JOptionPane.showMessageDialog(this,
                            "历史记录已成功导出至：\n" + file.getAbsolutePath());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "导出失败，请检查文件权限。",
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel.add(exportBtn);

        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> refresh());
        buttonPanel.add(refreshBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        refresh();
    }

    /**
     * 刷新历史记录显示。
     */
    public void refresh() {
        StringBuilder sb = new StringBuilder();
        java.util.List<String> list = historyManager.getHistoryList();
        int idx = 1;
        for (String record : list) {
            sb.append(idx++).append(". ").append(record).append("\n");
        }
        historyArea.setText(sb.toString());
        // 滚动到底部
        historyArea.setCaretPosition(Math.max(0, historyArea.getDocument().getLength()));
        statusLabel.setText("共 " + list.size() + " 条记录");
    }

    /**
     * 获取历史显示区域（用于外部查询选中文本等）
     * @return JTextArea 实例
     */
    public JTextArea getTextArea() {
        return historyArea;
    }

    /**
     * 为自定义按钮添加监听器（供外部扩展）
     * @param name 按钮名称
     * @param action ActionListener
     */
    public void addButton(String name, ActionListener action) {
        JButton btn = new JButton(name);
        btn.addActionListener(action);
        // 添加到按钮面板 (可以从 panel 获取)
        Component southComponent = getComponent(getComponentCount() - 1); // 假定 South 是最后一个
        if (southComponent instanceof JPanel) {
            ((JPanel) southComponent).add(btn);
        }
    }
}