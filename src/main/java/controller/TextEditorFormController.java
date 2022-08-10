package controller;

import com.sun.jdi.CharValue;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

public class TextEditorFormController {
    public TextArea txtEditor;
    public MenuItem mnuNew;
    public MenuItem mnuOpen;
    public MenuItem mnuSave;
    public MenuItem mnuPrint;
    public MenuItem mnuClose;
    public MenuItem mnuCut;
    public MenuItem mnuCopy;
    public MenuItem mnuPaste;
    public MenuItem mnuSelectAll;
    public MenuItem mnuAbout;
    private String selectedText;

    private Clipboard clipboard;
    private ClipboardContent clipboardContent;

    public void initialize() {

        clipboard = Clipboard.getSystemClipboard();
        clipboardContent = new ClipboardContent();

        mnuNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                txtEditor.clear();
            }
        });

        mnuOpen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                        "Text files(*.txt)/(*.dep9)", "*.dep9", "*.txt"));
                File file = fileChooser.showOpenDialog(txtEditor.getScene().getWindow());

                String fileName = file.getName();
                if(fileName.contains(".dep9")){
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        byte[] bytes = bis.readAllBytes();

                        for (int i = 0; i < bytes.length; i++) {
                            bytes[i] = (byte) (bytes[i] ^ 0B1111_1111);
                        }
                        String content = new String(bytes);
                        txtEditor.setText(content);
                        bis.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        byte[] bytes = bis.readAllBytes();
                        String content = new String(bytes);
                        txtEditor.setText(content);
                        bis.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        mnuSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Text File");
                fileChooser.setInitialFileName("text-editor.dep9");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                fileChooser.getExtensionFilters()
                        .add(new FileChooser.ExtensionFilter("Text files (*.dep9)", "*.dep9"));
                File saveLocation = fileChooser.showSaveDialog(txtEditor.getScene().getWindow());
                File savedFile = new File(String.valueOf(saveLocation));
                try {
                    if (!savedFile.exists()) {
                        savedFile.createNewFile();
                    } else {
                        Optional<ButtonType> result = new Alert(Alert.AlertType.INFORMATION, "File already exists. Do you want to overwrite?", ButtonType.YES, ButtonType.NO).showAndWait();
                        if (result.get() == ButtonType.NO) return;
                    }
                    byte[] bytes = txtEditor.getText().getBytes();
                    FileOutputStream fos = new FileOutputStream(savedFile);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    for (int i = 0; i < bytes.length; i++) {
                        bytes[i] = (byte) (bytes[i] ^ 0B1111_1111);
                    }
                    bos.write(bytes);
                    bos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        mnuPrint.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (Printer.getDefaultPrinter() == null) {
                    new Alert(Alert.AlertType.ERROR, "No default printer has been selected").showAndWait();
                    return;
                }

                PrinterJob printerJob = PrinterJob.createPrinterJob();
                if (printerJob != null) {
                    printerJob.showPageSetupDialog(txtEditor.getScene().getWindow());
                    boolean success = printerJob.printPage(txtEditor);
                    if (success) {
                        printerJob.endJob();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to print. Try again.").showAndWait();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to initialize a new printer job").show();
                }
            }
        });

        mnuClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Platform.exit();
            }
        });

        mnuAbout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                URL aboutForm = this.getClass().getResource("/view/AboutForm.fxml");
                try {
                    Parent aboutContainer = FXMLLoader.load(aboutForm);
                    Scene aboutScene = new Scene(aboutContainer);
                    Stage stage = new Stage();
                    stage.setScene(aboutScene);
                    stage.setTitle("About");
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.centerOnScreen();
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        mnuCut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String selectedContent = txtEditor.getSelectedText();
                clipboardContent.putString(selectedContent);
                clipboard.setContent(clipboardContent);
                String wholeContent = txtEditor.getText();
                IndexRange selection = txtEditor.getSelection();
                int start = selection.getStart();
                int end = selection.getEnd();
                String substring1 = wholeContent.substring(0, start);
                String substring2 = wholeContent.substring(end, wholeContent.length());
                txtEditor.clear();
                txtEditor.setText(substring1 + substring2);
            }
        });
        mnuCopy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                selectedText = txtEditor.getSelectedText();
                clipboardContent.putString(selectedText);
                clipboard.setContent(clipboardContent);
            }
        });
        mnuPaste.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                txtEditor.setText(clipboardContent.getString());
                clipboardContent.clear();
            }
        });
        mnuSelectAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                txtEditor.selectAll();
            }
        });
    }
}
