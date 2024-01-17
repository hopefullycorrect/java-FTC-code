/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aprioriproject;
import java.io.*;
import java.util.*;
import javax.swing.*;
/**
 *
 * @author harta
 */
public class Database {
    public Properties mypanel;
    private String strNamaPanel;
    public Database(){
}
public String SettingPanel(String nmPanel){
    try{
        mypanel = new Properties();
        mypanel.load(new FileInputStream("lib/Database.ini"));
        strNamaPanel = mypanel.getProperty(nmPanel);
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "tidak ada koneksi", "error", JOptionPane.INFORMATION_MESSAGE);
        System.err.println(e.getMessage());
        System.exit(0);
    }
    return strNamaPanel;
   }
}
