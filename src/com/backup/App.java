package com.backup;

import com.backup.db.File;
import com.backup.db.Folder;

import javax.swing.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class App implements Scanned {
    private JPanel main_panel;
    private JList <String>folder_list;
    private JButton addFolder;
    private JPanel bar_panel;
    private JLabel bar_label;
    private JTextArea log;
    private JButton rescan;
    private DefaultListModel<String> listModel;
    private Storage storage;
    private Scanner scanner;

    private App() {
        listModel = new DefaultListModel<>();
        folder_list.setModel(listModel);

        try {
            this.storage = new Storage();

            List<Folder> folders = Folder.getAll(storage.getConnection());
            for (Folder folder: folders) {
                listModel.addElement(folder.getPath());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        scanner = new Scanner(this);
        new Thread(scanner).start();

        addFolder.addActionListener((ActionEvent e) -> {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new java.io.File("."));
            fc.setDialogTitle("Choose folder to backup");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setAcceptAllFileFilterUsed(false);
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getAbsolutePath();
                try {
                    if (Folder.exists(path, storage.getConnection())) {
                        JOptionPane.showMessageDialog(null, "Folder already added");
                        return;
                    }

                    listModel.addElement(path);
                    this.scanner.addFolder(new Folder(path, this.storage.getConnection()));

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }

            }
        });

        rescan.addActionListener((ActionEvent e) -> {
            try {
                List<Folder> folders = Folder.getAll(storage.getConnection());
                this.scanner.addFolders(folders);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
    }

    public void folderStarted(Folder folder) {
        bar_label.setText(folder.getPath() + " scanning...");
    }

    public void folderScanned(Folder folder) {
        try {
            bar_label.setText(folder.getPath() + " done.");
            folder.setUpdatedAt();
            Folder.getDao(storage.getConnection()).update(folder);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void fileScanned(File file) {
        try {
            file.updateHash(storage.getConnection());
            log.insert(file.getHash() + " " + file.getPath() + "\r\n", 0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().main_panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
