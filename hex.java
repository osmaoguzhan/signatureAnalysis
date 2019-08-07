package com.hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class hex extends DataBase {

    PreparedStatement preparedstatement;

    public static StringBuilder convertToHex(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        int bytesCounter = 0;
        int value = 0;
        StringBuilder sbHex = new StringBuilder();
        StringBuilder sbResult = new StringBuilder();
        while ((value = is.read()) != -1) {
            sbHex.append(String.format("%02X ", value));
            if (bytesCounter == 15) {
                sbResult.append(sbHex).append("\n");
                sbHex.setLength(0);
                bytesCounter = 0;
            } else {
                bytesCounter++;
            }
        }
        if (bytesCounter != 0) {
            for (; bytesCounter < 16; bytesCounter++) {
                sbHex.append("   ");
            }
            sbResult.append(sbHex).append("\n");
        }
        StringBuilder deneme = sbResult;
        is.close();
        return deneme;
    }

    public static void main(String[] args) {
        hex hex = new hex();
        hex.run();

    }

    public void run() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please type the path you'd like to scan:");
        String path = scan.nextLine();
        signature(path);
    }

    public void signature(String path) {
        String extension;
        StringBuilder hex = null;
        File folder = new File(path);
        File[] files = folder.listFiles();
        if (files.length == 0) {
            System.out.println("This folder is empty. Please choose a folder that is not empty!!");
        } else {
            for (File file : files) {
                String newPath = path + "\\" + file.getName();
                extension = newPath.substring(newPath.lastIndexOf(".") + 1);
                try {
                    hex = convertToHex(new File(newPath));
                    getSignature(hex, extension);
                } catch (IOException ex) {
                    Logger.getLogger(hex.class.getName()).log(Level.SEVERE, null, ex);
                }
                newPath = path;
            }
        }
    }

    public void getSignature(StringBuilder hex, String extension) {
        String extDB, description, hexDB;
        try {
            connection_open();
            String query = "select hex,description,ext from signaturedb";
            preparedstatement = connection.prepareStatement(query);
            ResultSet rs = preparedstatement.executeQuery();
            while (rs.next()) {
                hexDB = rs.getString("hex");
                description = rs.getString("description");
                extDB = rs.getString("ext");
                //System.out.println(extDB);
                String control = hex.substring(0, hexDB.length()).trim();
                //System.out.println(control);
                if (control.equals(hexDB)) {
                    if (extension.equalsIgnoreCase(extDB)) {
                        System.out.println("Extensions got matched!!");
                        System.out.println("File's description is " + description);
                        System.out.println("File's extension is " + extension + " and real one is " + extDB);
                        System.out.println("---------------------------------------------------------------");
                    } else {
                        System.out.println("Extensions didn't match!!");
                        System.out.println("File's extension is " + extension + " but real one is " + extDB);
                        System.out.println("----------------------------------------------------------------");

                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(hex.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            connection_close();
        }
    }
}
