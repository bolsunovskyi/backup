package com.backup;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class App {
    private JPanel main_panel;
    private JList <String>folder_list;
    private JButton addFolder;
    private JPanel bar_panel;
    private JLabel bar_label;
    private JTextArea log;
    private DefaultListModel<String> listModel;

    private App() {
        listModel = new DefaultListModel<>();
        folder_list.setModel(listModel);

        addFolder.addActionListener((ActionEvent e) -> {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new java.io.File("."));
            fc.setDialogTitle("Choose folder to backup");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setAcceptAllFileFilterUsed(false);
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getAbsolutePath();

                listModel.addElement(path);
                bar_label.setText(path + " scanning...");
                Scanner s = new Scanner(path, this);
                new Thread(s).start();
            }
        });
    }

    public void scanDone(String folder) {
        bar_label.setText(folder + " done.");
    }

    public void scanFile(String file) {
        log.insert(file + "\r\n", 0);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().main_panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
