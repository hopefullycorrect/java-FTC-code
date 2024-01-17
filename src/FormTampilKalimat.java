/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author harta
 */
public class FormTampilKalimat extends javax.swing.JFrame {
    private final JFileChooser  openFileChooser;
    /**
     * Creates new form FormTampilKalimat
     */
    private int getLastUsedIDFromDatabase() {
        int lastUsedID = 0;
        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            ResultSet res = stt.executeQuery("SELECT MAX(id_tweet) FROM tb_tweet");

            if (res.next()) {
                lastUsedID = res.getInt(1);
            }

            res.close();
            stt.close();
            kon.close();
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
        }
        return lastUsedID;
    }

    private void setAutoIncrementValue(int lastUsedID) {
        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String alterTableSQL = "ALTER TABLE tb_tweet AUTO_INCREMENT = " + lastUsedID;
            stt.executeUpdate(alterTableSQL);
            stt.close();
            kon.close();
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
        }
    }

    
    Database dbsetting; String driver, database, user, pass;
    public FormTampilKalimat() {
        initComponents();
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(380, 130, 110, 23);
        getContentPane().add(txtpath);
        txtpath.setBounds(170, 130, 190, 22);
        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("c:\\temp"));
        openFileChooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        dbsetting = new Database();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");
        this.setSize(600,700);//lebar, tinggi
        tabel.setModel(tblModel);
        Tabel(tabel,new int[]{120,120,180,120});
        tabel2.setModel(tblModeltabel2);
        this.setSize(600,700);//lebar, tinggi
        Tabel(tabel2,new int[]{120,120,180,120});
        tabel3.setModel(tblModeltabel3);
        this.setSize(600,700);//lebar, tinggi
        Tabel(tabel3,new int[]{120,120,180,120});
        setDefaultTable();
        cetakIndeksKata();
        cetakKalimatpertweet();
        katasesuaiminsup();
        HitungEO();
    }
    private void importFile() {
        int lastUsedID = getLastUsedIDFromDatabase();
        setAutoIncrementValue(lastUsedID);
    int returnValue = openFileChooser.showOpenDialog(this);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = openFileChooser.getSelectedFile();
        txtpath.setText(selectedFile.getAbsolutePath());

        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
                insertIntoDatabase(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "eror membaca txt: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "tidak ada file dipilih", "Warning", JOptionPane.WARNING_MESSAGE);
    }
}

private void insertIntoDatabase(String line) {
        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL = "INSERT INTO tb_tweet (`Created At`, id_tweet, text, username)"
                    + "VALUES (CURRENT_TIMESTAMP,NULL, '" + line + "', 'harta')";
            stt.executeUpdate(SQL);
            stt.close();
            kon.close();
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
        }
    }
    String[] data = new String[4];
    ArrayList<String> term = new ArrayList<String>();
    ArrayList<String> kalimat = new ArrayList<String>();
    ArrayList<String> resultList = new ArrayList<>();
    
    public void setDefaultTable(){
        String stat="";
        try{
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL =  "Select * from tb_tweet";
            ResultSet res = stt.executeQuery(SQL);
            String[] kata;
            int i=0,jum=0;
            while(res.next()){//loop setiap baris di MySQL
                data[0]= res.getString(1);
                data[1]= res.getString(2);
                data[2]= res.getString(3);//kalimat
                //add kalimat ke ArrayList
                kalimat.add(data[2]);
                kata = data[2].split(" ");
                for(int j=0;j<kata.length;j++){
                  term.add(kata[j]);
                }
                System.out.println("===========");
                data[3]= res.getString(4);
                tblModel.addRow(data);
                i++;
                jum++;
            }
            term = (ArrayList<String>) term.stream().distinct().collect(Collectors.toList());
        for (i = 0; i < kalimat.size(); i++) {
            StringBuilder rowStringBuilder = new StringBuilder();
            rowStringBuilder.append("T" + i + " "); // tambah T1, T2, dst.
            for (int k = 0; k < term.size(); k++) { // loop all kata
                if (kalimat.get(i).contains(term.get(k).toString())) {
                    rowStringBuilder.append("1 ");
                } else {
                    rowStringBuilder.append("0 ");
                }
            }
            System.out.println(""); String rowString = rowStringBuilder.toString();
            System.out.println(rowString); resultList.add(rowString);
            String[] rowData = rowString.split(" ");
            if (tblModeltabel3.getColumnCount() < rowData.length) {
                Object[] columnNames = new Object[term.size() + 1];
                columnNames[0] = "<html><center><br>Kata<br>Kalimat</center></html>" + "\nKata";
                for (int k = 0; k < term.size(); k++) {
                    columnNames[k + 1] = term.get(k); }
                tblModeltabel3.setColumnIdentifiers(columnNames);
            }
            tblModeltabel3.addRow(rowData);
        }
        tabel3.setModel(tblModeltabel3);
        res.close(); stt.close(); kon.close();
    } catch (Exception exc) {
        System.err.println(exc.getMessage());
    }
}
   
    public void cetakIndeksKata(){
        term = (ArrayList<String>) term.stream().distinct().collect(Collectors.toList());
        for(int k = 0; k<term.size(); k++){
           System.out.println("term-"+k+":" +term.get(k));
        }
    }//    public void setvectorcandidate(){ }
    HashMap<String, Integer> katasesuaiMinsup = new HashMap<String, Integer>();
    public Map<String, ArrayList<String>> multiValueMap = new HashMap<String, ArrayList<String>>();
    int minsupport = 5;
    String[][] klasterkandidat = new String[200][200]; 
    
    //minimal mucul di 5 kalimat; minsup 50%
    //cari kata kemunculannya di 5 kalimat
    public void katasesuaiminsup(){
        int counter;
term = (ArrayList<String>) term.stream().distinct().collect(Collectors.toList());
        for(int k = 0; k<term.size(); k++){
            System.out.println("term-"+k+":"+ term.get(k)+""); //loop kalimat
            counter = 0;
            for(int i=0; i<kalimat.size(); i++){
                if(kalimat.get(i).contains(term.get(k))){
                counter = counter+1;    
            }
        } //System.out.println("");
        katasesuaiMinsup.put(term.get(k), counter);
        } int j = 0;
        for (String k : katasesuaiMinsup.keySet()){
            System.out.println("\nterm : " +k+ " - value : " + katasesuaiMinsup.get(k));
            //cetak kata yang lebih dari 3 kalimat
            if(katasesuaiMinsup.get(k)>=minsupport){
                multiValueMap.put(k,  new ArrayList<String>());
                for(int i = 0; i<kalimat.size(); i++){
                    if(kalimat.get(i).contains(k)){
                  System.out.println("T" + i + " "); multiValueMap.get(k).add("T" + i);
                } 
            } //end for  
        } //end if
            j++;   
    } //CETAK ISI MULTIHASHMAP
    multiValueMap.forEach((key, value) -> {
    System.out.println(key + " " + value);
}); }
    
    public int cekFrekuensiTweet(Object o){
        int counter=0;
        for (String k : multiValueMap.keySet()){ //loop key
            for( int i = 0; i<multiValueMap.get(k).size(); i++){
                //System.out.print(multiValueMap.get(k).get(i));
                if(multiValueMap.get(k).get(i).equals(o.toString())){
                    counter++;
                }
        } //System.out.println("");
    } return counter;
    }
    
        public void HitungEO(){
        double EO = 0; double var = 0; double jumTweet = 0; int counter=0; int j = 1;
        for (String k : multiValueMap.keySet()){ //loop key
            EO = 0;
            for( int i = 0; i<multiValueMap.get(k).size(); i++){
                //System.out.print(multiValueMap.get(k).get(i));
                jumTweet = cekFrekuensiTweet(multiValueMap.get(k).get(i));
                var = 1/jumTweet;
                EO += (-1*var* Math.log(var));
                counter = j;        
        } j++; 
        System.out.println("key = " +k+" EO = " + EO);
        tblModeltabel2.addRow(new Object[]{counter,
            String.format("{%s}", k), multiValueMap.get(k), EO });
        System.out.println("");  
    }
        tabel2.setModel(tblModeltabel2);
        tabel3.setModel(tblModeltabel3);
    }
        
    public void cetakKalimatpertweet(){
        term = (ArrayList<String>) term.stream().distinct().collect(Collectors.toList());
        for(int k = 0; k<term.size(); k++){
            System.out.print("term-"+k+":"+ term.get(k)+" - kalimat:");
            for(int i=0; i<kalimat.size(); i++){
                if(kalimat.get(i).contains(term.get(k))){
                System.out.print("T"+i+" ");
            }
        } System.out.println("");
        }
    }
    public String cekKataPerkalimat(String kata){
        String stat="";
        System.out.println("kata="+kata);
        try{
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL =  "Select text from tb_tweet";
            ResultSet res = stt.executeQuery(SQL);
            int i=0;
            //ArrayList<String> flag = new ArrayList<String>();
            while(res.next()){//loop kalimat setiap baris di MySQL
                data[0]= res.getString("text"); //System.out.println("Kalimat-"+i+"-"+data[0]);
                if(data[0].contains(kata)){//cek kalimat berisi kata
                   stat= "1 "; //  flag.add("1");
                }else
                   stat= "0 "; // flag.add("0");
                i++;
            }//System.out.print(" ");
            res.close(); stt.close(); kon.close();
        }catch(Exception exc){
            System.err.println(exc.getMessage());
        }
        return stat;
    }
    
        
    private void Tabel(javax.swing.JTable tb,int lebar[]){
        tb.setAutoResizeMode(tb.AUTO_RESIZE_OFF);
        int kolom = tb.getColumnCount();
        for(int i=0;i<kolom;i++){
            javax.swing.table.TableColumn tbc = tb.getColumnModel().getColumn(i);
            tbc.setPreferredWidth(lebar[i]);
            tb.setRowHeight(17);
        }
    }
    
    private javax.swing.table.DefaultTableModel getDefaultTabelModel(){
        return new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[] {"Created at","id_tweet","text","username"}
        ){
            boolean[] canEdit= new boolean[]{
                false,false,false,false
            };
            public boolean isCellEditable(int rowIndex, int columnIndex){
                return canEdit[columnIndex];
            }
        };
    }
    
    private javax.swing.table.DefaultTableModel getDefaultTabelModel2(){
        return new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[] {"Nomor","Frequent Term","Cluster Candidate","EO"}
        ){
            boolean[] canEdit= new boolean[]{
                false,false,false,false
            };
            public boolean isCellEditable(int rowIndex, int columnIndex){
                return canEdit[columnIndex];
            }
        };
    }
    private javax.swing.table.DefaultTableModel getDefaultTabelModel3(){
        return new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[] {"Nomor"}
        ){
            boolean[] canEdit= new boolean[]{
                false,false,false,false
            };
            public boolean isCellEditable(int rowIndex, int columnIndex){
                return canEdit[columnIndex];
            }
        };
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        txtpath = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel2 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel3 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Master Kalimat data Twitter");
        jLabel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        getContentPane().add(jLabel1);
        jLabel1.setBounds(100, 40, 430, 60);

        jLabel2.setText("Upload File");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(60, 130, 80, 20);

        jButton1.setText("Browse");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(380, 130, 110, 23);
        getContentPane().add(txtpath);
        txtpath.setBounds(170, 130, 190, 22);

        tabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Created At", "id_tweet", "text", "username"
            }
        ));
        jScrollPane1.setViewportView(tabel);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(50, 190, 520, 160);

        tabel2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        jScrollPane2.setViewportView(tabel2);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(610, 50, 452, 160);

        tabel3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabel3.setShowHorizontalLines(true);
        tabel3.setShowVerticalLines(true);
        jScrollPane3.setViewportView(tabel3);
        tabel3.getAccessibleContext().setAccessibleParent(jScrollPane1);

        getContentPane().add(jScrollPane3);
        jScrollPane3.setBounds(50, 390, 1130, 200);

        jLabel3.setText("iterasi kata per-kalimat");
        jLabel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        getContentPane().add(jLabel3);
        jLabel3.setBounds(550, 360, 130, 16);

        jLabel4.setText("Hasil Hitung EO minsup 50%");
        jLabel4.setToolTipText("");
        jLabel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        getContentPane().add(jLabel4);
        jLabel4.setBounds(760, 30, 170, 16);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    importFile();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormTampilKalimat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormTampilKalimat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormTampilKalimat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormTampilKalimat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormTampilKalimat().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tabel;
    private javax.swing.JTable tabel2;
    private javax.swing.JTable tabel3;
    private javax.swing.JTextField txtpath;
    // End of variables declaration//GEN-END:variables
    private javax.swing.table.DefaultTableModel tblModel = getDefaultTabelModel(); 
    private javax.swing.table.DefaultTableModel tblModeltabel2 = getDefaultTabelModel2();
    private javax.swing.table.DefaultTableModel tblModeltabel3 = getDefaultTabelModel3();
}
