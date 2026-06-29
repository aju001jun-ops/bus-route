/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package conductor_page;

/**
 *
 * @author ELCOT
 */

import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;
public class conductor_main extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(conductor_main.class.getName());

    /**
     * Creates new form conductor_main
     */
    private int conductorId;
private int assignmentId;
private int scheduleId;
private int routeId;
private String routeName;
private String busNumber;
 private ArrayList<JButton> stopButtons = new ArrayList<>();
    private ArrayList<Integer> stopIds = new ArrayList<>();
    
    public conductor_main() {
        initComponents();
    }
    public conductor_main(int conductorId,int assignmentId,int scheduleId,int routeId,String routeName,String busNumber){
        
        this.conductorId=conductorId;
        this.assignmentId=assignmentId;
        this.scheduleId=scheduleId;
        this.routeId=routeId;;
        this.routeName=routeName;
        this.busNumber=busNumber;
        initComponents();
        lblRoute.setText("Route: " + routeName);
        lblBus.setText("Bus: " + busNumber);

        stopsPanel.setLayout(new javax.swing.BoxLayout(stopsPanel, javax.swing.BoxLayout.Y_AXIS));

        loadStops();
        
    }
        private void loadStops() {

        stopsPanel.removeAll();
        stopButtons.clear();
        stopIds.clear();

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bus_booking_db", "root", "password");

            CallableStatement cs = conn.prepareCall("{CALL sp_conductor_get_route_stops(?)}");
            cs.setInt(1, routeId);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {

                int stopOrder = rs.getInt("stop_order");
                int stopId = rs.getInt("stop_id");
                String stopName = rs.getString("stop_name");

                JButton btn = new JButton(stopOrder + ". " + stopName);
                btn.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
                btn.setMaximumSize(new java.awt.Dimension(Short.MAX_VALUE, 40));

                final int currentStopId = stopId;

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            markStopReached(currentStopId);
                        }
                    }
                });

                stopsPanel.add(btn);
                stopsPanel.add(javax.swing.Box.createVerticalStrut(6));

                stopButtons.add(btn);
                stopIds.add(stopId);
            }

            cs.close();
            conn.close();

            stopsPanel.revalidate();
            stopsPanel.repaint();

            highlightCurrentStop();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading stops: " + e.getMessage());
        }
    }

    private void markStopReached(int stopId) {

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bus_booking_db", "root", "password");

            CallableStatement cs = conn.prepareCall(
                "{CALL sp_conductor_reached_stop(?, ?, ?, ?, ?, ?)}");
            cs.setInt(1, assignmentId);
            cs.setInt(2, scheduleId);
            cs.setInt(3, stopId);
            cs.setInt(4, 0);
            cs.setString(5, "");
            cs.registerOutParameter(6, Types.VARCHAR);
            cs.execute();

            String nextStop = cs.getString(6);

            cs.close();
            conn.close();

            JOptionPane.showMessageDialog(this, "Marked reached. Next stop: " + nextStop);

            highlightCurrentStop();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void highlightCurrentStop() {

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bus_booking_db", "root", "password");

            CallableStatement cs = conn.prepareCall(
                "{CALL sp_conductor_get_current_status(?)}");
            cs.setInt(1, scheduleId);
            ResultSet rs = cs.executeQuery();

            String currentStop = null;
            String nextStop = null;

            if (rs.next()) {
                currentStop = rs.getString("current_stop");
                nextStop = rs.getString("next_stop");
            }

            cs.close();
            conn.close();

            for (JButton btn : stopButtons) {

                String label = btn.getText();
                btn.setOpaque(true);
                btn.setBorderPainted(true);

                if (currentStop != null && label.contains(currentStop)) {
                    btn.setBackground(new java.awt.Color(200, 255, 200));
                    btn.setEnabled(false);
                } else if (nextStop != null && label.contains(nextStop)) {
                    btn.setBackground(new java.awt.Color(255, 235, 180));
                    btn.setEnabled(true);
                } else {
                    btn.setBackground(new java.awt.Color(235, 235, 235));
                    btn.setEnabled(false);
                }
            }

            lblCurrentStop.setText("Current Stop: " + (currentStop != null ? currentStop : "Not started"));
            lblNextStop.setText("Next Stop: " + (nextStop != null ? nextStop : "-"));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblRoute = new javax.swing.JLabel();
        lblBus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        label5 = new javax.swing.JLabel();
        lblCurrentStop = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        stop_screen = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        stopsPanel = new javax.swing.JPanel();
        route_show = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        label6 = new javax.swing.JLabel();
        lblNextStop = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblRoute.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        lblBus.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblBus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lblRoute, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblRoute, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblBus, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 300, -1));

        jPanel2.setBackground(new java.awt.Color(153, 255, 153));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setForeground(new java.awt.Color(204, 255, 204));

        label5.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        label5.setForeground(new java.awt.Color(0, 51, 0));
        label5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/conductor_page/icons8-location-100.png"))); // NOI18N
        label5.setText("current stop");

        lblCurrentStop.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblCurrentStop, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label5))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCurrentStop, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 50, 260, 100));

        jPanel3.setBackground(new java.awt.Color(0, 204, 204));
        jPanel3.setToolTipText("");

        stop_screen.setBackground(new java.awt.Color(0, 255, 255));
        stop_screen.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        stop_screen.setForeground(new java.awt.Color(51, 51, 255));
        stop_screen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        stop_screen.setText("stops");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(stop_screen, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(stop_screen, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 180, 70, 30));

        route_show.setLayout(new javax.swing.BoxLayout(route_show, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout stopsPanelLayout = new javax.swing.GroupLayout(stopsPanel);
        stopsPanel.setLayout(stopsPanelLayout);
        stopsPanelLayout.setHorizontalGroup(
            stopsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stopsPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(route_show, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(203, Short.MAX_VALUE))
        );
        stopsPanelLayout.setVerticalGroup(
            stopsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stopsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(route_show, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(282, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(stopsPanel);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 220, 220, 290));

        jPanel5.setBackground(new java.awt.Color(255, 255, 153));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel5.setForeground(new java.awt.Color(204, 255, 204));

        label6.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        label6.setForeground(new java.awt.Color(0, 51, 0));
        label6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/conductor_page/icons8-location-100.png"))); // NOI18N
        label6.setText("next stop");

        lblNextStop.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNextStop, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label6))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNextStop, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 50, -1, -1));

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bus_route_booking/images/back-button.png"))); // NOI18N
        jButton2.addActionListener(this::jButton2ActionPerformed);
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/conductor_page/main1-bg.jpg"))); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(1280, 720));
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 560));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        conductor_entry frame=new conductor_entry();
        frame.setVisible(true);
        this.dispose();

    }//GEN-LAST:event_jButton2ActionPerformed

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new conductor_main().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label5;
    private javax.swing.JLabel label6;
    private javax.swing.JLabel lblBus;
    private javax.swing.JLabel lblCurrentStop;
    private javax.swing.JLabel lblNextStop;
    private javax.swing.JLabel lblRoute;
    private javax.swing.JPanel route_show;
    private javax.swing.JLabel stop_screen;
    private javax.swing.JPanel stopsPanel;
    // End of variables declaration//GEN-END:variables
}
