package org.app.fsu_bank_project;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;


import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    String email = "", Password = "";
    Socket clientSocket;
    BufferedReader in = null;
    PrintWriter out = null;
    @FXML
    private ChoiceBox<String> cb_menu;

    @FXML
    private ListView<String> lv_list;

    @FXML
    private Pane p_dotplot;

    @FXML
    private Label login_message;

    @FXML
    private TextArea ta_format;

    @FXML
    private Pane p_login;

    @FXML
    private Pane p_main;

    @FXML
    private Pane p_savenew;

    @FXML
    private Pane p_chpas;

    @FXML
    private TextField tf_newPass;

    @FXML
    private TextField tf_repeatPass;

    @FXML
    private TextField tf_email;

    @FXML
    private TextField tf_password;

    @FXML
    private TextField first_DP;

    @FXML
    private TextField second_DP;

    @FXML
    private Label newPassMessage;

    @FXML
    private Label Fsaved;

    @FXML
    private Label errorMessage;


    @FXML
    private TextField tf_Fname;


    @FXML
    void login(ActionEvent event) throws IOException {
        if(tf_email.getText().isEmpty() || tf_password.getText().isEmpty()) {
            return;
        }
        out.println(tf_email.getText());
        out.println(tf_password.getText());
        String res = in.readLine();

        if (res.contains("invalid")){
            login_message.setVisible(true);
            login_message.setText("invalid email");
        }else if (res.contains("Password for login is")){
            login_message.setVisible(true);
            login_message.setText(res);
        }else if(res.contains("Logged")){
            this.email = tf_email.getText();
            p_main.setVisible(true);
            p_login.setVisible(false);
        }else{
            login_message.setVisible(true);
            login_message.setText("Password is Wrong");
        }

    }

    @FXML
    void updatePassword(ActionEvent event) throws IOException {
        if(tf_newPass.getText().isEmpty() || tf_repeatPass.getText().isEmpty()) {
            return;
        }
        if(!tf_newPass.getText().equals(tf_repeatPass.getText())) {
            return;
        }
        new Thread(() -> {
            out.println("newpass");
            out.println(tf_newPass.getText());
            String mess = "";
            try {
                mess = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String finalMess = mess;
            Platform.runLater(() -> {
                newPassMessage.setVisible(true);
                newPassMessage.setText(finalMess);
            });

        }).start();

    }


    @FXML
    void saveFormat(ActionEvent event) throws IOException {
        Fsaved.setVisible(false);
        if (ta_format.getText().isEmpty() || tf_Fname.getText().isEmpty()) return;
        if (!(ta_format.getText().contains("XX") || ta_format.getText().contains("LOCUS"))) {
            Fsaved.setText("invalid Format");
            return;
        }
        out.println("save");
        out.println(tf_Fname.getText());
        out.println(ta_format.getText());
        Fsaved.setVisible(true);
    }

    @FXML
    void makeDotplot(ActionEvent event) throws IOException {
        if (first_DP.getText().isEmpty() || second_DP.getText().isEmpty()) return;
        out.println("dotplot");
        String firstF = first_DP.getText();
        String secondF = second_DP.getText();
        out.println(firstF);
        out.println(secondF);
        String response = in.readLine();
        while (response.equalsIgnoreCase("Enter a valid name!:")){
            errorMessage.setText("Enter a valid name!");
            firstF = first_DP.getText();
            secondF = second_DP.getText();
            out.println(firstF);
            out.println(secondF);
        }
        lv_list.getItems().clear();
        do {
            lv_list.getItems().add(response);
            response=in.readLine();
        } while (!response.endsWith("finished"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            clientSocket = new Socket("localhost", 1234);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        setup();
    }
    private void setup(){

        cb_menu.getItems().add("Change Password");
        cb_menu.getItems().add("Save new Format");
        cb_menu.getItems().add("Make Dotplot");
        newPassMessage.setVisible(false);
        cb_menu.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            if(cb_menu.getSelectionModel().getSelectedItem().equals("Change Password")){
                p_chpas.setVisible(true);
                p_savenew.setVisible(false);
                p_dotplot.setVisible(false);
                Fsaved.setVisible(false);
            }else if(cb_menu.getSelectionModel().getSelectedItem().equals("Save new Format")){
                newPassMessage.setVisible(false);
                p_chpas.setVisible(false);
                p_savenew.setVisible(true);
                p_dotplot.setVisible(false);
            }else if(cb_menu.getSelectionModel().getSelectedItem().equals("Make Dotplot")){
                updateList();
                newPassMessage.setVisible(false);
                Fsaved.setVisible(false);
                p_chpas.setVisible(false);
                p_savenew.setVisible(false);
                p_dotplot.setVisible(true);
            }
        });
        cb_menu.getSelectionModel().select(1);
    }
    private void updateList(){
        lv_list.getItems().clear();
        out.println("update");
        String list;
        do {
            try {
                list = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            lv_list.setEditable(true);
            lv_list.getItems().add(list);
        } while (!list.equals("finished"));
    }
}