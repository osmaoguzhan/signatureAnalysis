package com.hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
                System.out.println("FILE NAME IS " + file);
                try {
                    hex = convertToHex(new File(newPath));
                    if (!extension.equalsIgnoreCase("txt")) {
                        getSignature(hex, extension,file.getName());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(hex.class.getName()).log(Level.SEVERE, null, ex);
                }
                newPath = path;
            }
        }
    }

    public void getSignature(StringBuilder hex, String extension,String file) {
        List description = new ArrayList();
        List hexDB = new ArrayList();
        List extDB = new ArrayList();
        try {
            connection_open();
            String query = "select hex,description,ext from signaturedb";
            preparedstatement = connection.prepareStatement(query);
            ResultSet rs = preparedstatement.executeQuery();
            while (rs.next()) {
                hexDB.add(rs.getString("hex"));
                description.add(rs.getString("description"));
                extDB.add(rs.getString("ext"));
            }
        } catch (SQLException e) {
            Logger.getLogger(hex.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            connection_close();
        }
        match(hexDB, extDB, description, hex, extension,file);

    }

    public void match(List hexDB, List extDB, List description, StringBuilder hex, String ext,String file) {
        int counter = 0;
        for (int i = 0; i < hexDB.size(); i++) {
            String control = hex.substring(0, hexDB.get(i).toString().length());
            if (control.equalsIgnoreCase(hexDB.get(i).toString())) {
                if (!extDB.get(i).toString().equalsIgnoreCase(ext)) {
                    System.out.println("\u001b[41mDoesn't Match!!");
                    System.out.println("\u001b[41mReal extension :" + extDB.get(i));
                   // writeHTML(file,ext,extDB.get(i).toString(),description.get(i).toString());
                } else {
                    System.out.println("\u001b[42mEverything is OK!! There is no manipulation!!");
                    System.out.println("----------------------------------------------");
                    //writeHTML(file,ext,extDB.get(i).toString(),description.get(i).toString());
                }
            } else {
                counter++;
            }
            if (counter == hexDB.size()) {
                System.out.println("\u001b[41mThe signature couldn't found on DB!!");
                System.out.println("--------------------------------------------------");
                //writeHTML(file,ext,"Not Found on DB","Not Found on DB");
            }
        }

    }
   /* public void writeHTML(String fileName,String extension,String realExt,String Description){
        
    }*/
}
