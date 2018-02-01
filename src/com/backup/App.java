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
    private JButton remove_folder;
    private JButton scan_folder;
    private JTextArea numbersArea;
    private JCheckBox scan_update;
    private JButton create_archive;
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
                    scan_folder.setEnabled(false);
                    rescan.setEnabled(false);

                } catch (SQLException ex) {
                    log.append(ex.getMessage());
                }

            }
        });

        rescan.addActionListener((ActionEvent e) -> {
            log.setText("");
            rescan.setEnabled(false);
            scan_folder.setEnabled(false);
            try {
                List<Folder> folders = Folder.getAll(storage.getConnection());
                this.scanner.addFolders(folders);
            } catch (SQLException ex) {
                log.append(ex.getMessage());
            }
        });

        remove_folder.addActionListener((ActionEvent e) -> {
            int index = folder_list.getSelectedIndex();
            if (index == -1) {
                return;
            }

            if (JOptionPane.showConfirmDialog(
                    null,
                    "Do you really want to delete selected folder ?") == JOptionPane.OK_OPTION) {

                String folderPath = listModel.get(index);
                try {
                    rescan.setEnabled(false);
                    scan_folder.setEnabled(false);
                    Folder.remove(storage.getConnection(), folderPath);
                    listModel.remove(index);
                    updateNumbers();
                } catch (SQLException ex) {
                    log.append(ex.getMessage());
                }
            }
        });
        scan_folder.addActionListener((ActionEvent e) -> {
            int index = folder_list.getSelectedIndex();
            if (index == -1) {
                return;
            }

            try {
                String folderPath = listModel.get(index);
                Folder folder = Folder.getByPath(storage.getConnection(), folderPath);
                if (folder != null) {
                    scan_folder.setEnabled(false);
                    rescan.setEnabled(false);
                    scanner.addFolder(folder);
                }
            } catch (SQLException ex) {
                log.append(ex.getMessage());
            }
        });
    }

    public void folderStarted(Folder folder) {
        bar_label.setText(folder.getPath() + " indexing...");
    }

    public void fileIndexed(Folder f, long n) {
        bar_label.setText(f.getPath() + " indexing... Files count: " + n);
    }

    public void folderScanned(Folder folder) {
        try {
            bar_label.setText(folder.getPath() + " done.");
            folder.setUpdatedAt();
            Folder.getDao(storage.getConnection()).update(folder);
        } catch (SQLException e) {
            log.append(e.getMessage());
        }
    }

    public void fileScanned(File file, long progress, long total) {
        try {
            file.updateHash(storage.getConnection());
            bar_label.setText(String.format("Files scan: %d/%d", progress, total));


            if (scan_update.isSelected()) {
                updateNumbers();
            }
        } catch (SQLException e) {
            log.append(e.getMessage());
        }

    }

    private void updateNumbers() {
        try {
            Storage.Numbers n = this.storage.getNumbers();
            numbersArea.setText(String.format("Total files count: %d\nTotal files size: %s\nUpdated files count: %d\nUpdated files size: %s",
                    n.files, n.getFilesSizeStr(), n.changed, n.getChangedFilesSizeStr()));
        } catch (SQLException e) {
            log.append(e.getMessage());
        }
    }

    public void queueFinished() {
        rescan.setEnabled(true);
        scan_folder.setEnabled(true);
        updateNumbers();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().main_panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
