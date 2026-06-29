/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package user;

/**
 *
 * @author ELCOT
 */
import java.sql.*;
import javax.swing.JOptionPane;

public class bus_route extends javax.swing.JFrame {

    private int scheduleId;
    private String busNumber;
    private String fromStop;
    private String toStop;
    private javax.swing.Timer liveTimer;

    // store stop data for drawing
    private java.util.ArrayList<String> stopNames = new java.util.ArrayList<>();
    private java.util.ArrayList<Integer> stopIds = new java.util.ArrayList<>();
    private int currentStopIndex = -1;
    private int nextStopIndex = -1;
    private int progressPct = 0;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(bus_route.class.getName());

    /**
     * Creates new form bus_route
     */
   
    public bus_route(int scheduleId, String busNumber, String fromStop, String toStop) {
        this.scheduleId = scheduleId;
        this.busNumber = busNumber;
        this.fromStop = fromStop;
        this.toStop = toStop;

        initComponents();

        from_input.setText(fromStop);
        to_input.setText(toStop);
         current.setText("Current Stop: Loading...");


        loadRouteStops();
        loadLivePosition();

        liveTimer = new javax.swing.Timer(30000, e -> {
            loadLivePosition();
            mapPanel.repaint();
        });
        liveTimer.start();

        // optional - show basic info immediately
        //lblBusNumber.setText("Bus: " + busNumber);
        //lblRoute.setText(fromStop + " → " + toStop);
    }
     public bus_route(){
        initComponents();
    }

    private void loadRouteStops() {
        stopNames.clear();
        stopIds.clear();

        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bus_booking_db", "root", "password");

            // get route_id from schedule
            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT route_id FROM local_schedule WHERE schedule_id = ?");
            ps1.setInt(1, scheduleId);
            ResultSet rs1 = ps1.executeQuery();

            if (!rs1.next()) {
                conn.close();
                return;
            }
            int routeId = rs1.getInt("route_id");
            ps1.close();

            // get all stops in order
            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT rs.stop_id, s.stop_name "
                    + "FROM local_route_stop rs "
                    + "JOIN shared_stop s ON s.stop_id = rs.stop_id "
                    + "WHERE rs.route_id = ? "
                    + "ORDER BY rs.stop_order");
            ps2.setInt(1, routeId);
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
                stopNames.add(rs2.getString("stop_name"));
                stopIds.add(rs2.getInt("stop_id"));
            }

            ps2.close();
            conn.close();

            mapPanel.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadLivePosition() {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bus_booking_db", "root", "password");

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT current_stop_id, next_stop_id, progress_pct, delay_mins "
                    + "FROM local_live_position "
                    + "WHERE schedule_id = ? AND travel_date = CURDATE()");
            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int currentStopId = rs.getInt("current_stop_id");
                int nextStopId = rs.getInt("next_stop_id");
                progressPct = rs.getInt("progress_pct");
                int delayMins = rs.getInt("delay_mins");

                currentStopIndex = stopIds.indexOf(currentStopId);
                nextStopIndex = stopIds.indexOf(nextStopId);

                String currentStopName = currentStopIndex >= 0
                        ? stopNames.get(currentStopIndex) : "Not started";

                current.setText("Current Stop: " + currentStopName
                        + (delayMins > 0 ? " (+" + delayMins + " mins late)" : ""));
            } else {
                current.setText("Current Stop: Not started yet");
            }

            ps.close();
            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // override paint in your mapPanel
    // In NetBeans Design view: right click mapPanel → Customize Code
    // change initialization to:
    // mapPanel = new javax.swing.JPanel() {
    //     protected void paintComponent(java.awt.Graphics g) {
    //         super.paintComponent(g);
    //         drawRoute(g);
    //     }
    // };
    private void drawRoute(java.awt.Graphics g) {

        if (stopNames.isEmpty()) {
            return;
        }

        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = mapPanel.getWidth();
        int panelHeight = mapPanel.getHeight();

        int totalStops = stopNames.size();
        int spacing = (panelHeight - 80) / (totalStops - 1);
        int centerX = panelWidth / 2;
        int startY = 40;

        // calculate Y position for each stop
        int[] stopY = new int[totalStops];
        for (int i = 0; i < totalStops; i++) {
            stopY[i] = startY + i * spacing;
        }

        // draw route line (grey background)
        g2.setColor(new java.awt.Color(200, 200, 200));
        g2.setStroke(new java.awt.BasicStroke(4));
        g2.drawLine(centerX, stopY[0], centerX, stopY[totalStops - 1]);

        // draw passed section (green line)
        if (currentStopIndex > 0) {
            g2.setColor(new java.awt.Color(0, 180, 0));
            g2.setStroke(new java.awt.BasicStroke(4));
            g2.drawLine(centerX, stopY[0], centerX, stopY[currentStopIndex]);
        }

        // draw each stop circle and name
        for (int i = 0; i < totalStops; i++) {

            String name = stopNames.get(i);
            boolean isCurrent = (i == currentStopIndex);
            boolean isNext = (i == nextStopIndex);
            boolean isPassed = (i < currentStopIndex);
            boolean isFromStop = name.equals(fromStop);
            boolean isToStop = name.equals(toStop);

            // stop circle color
            if (isCurrent) {
                g2.setColor(new java.awt.Color(0, 150, 0));    // green - current
            } else if (isNext) {
                g2.setColor(new java.awt.Color(255, 160, 0));  // orange - next
            } else if (isPassed) {
                g2.setColor(new java.awt.Color(150, 150, 150)); // grey - passed
            } else if (isFromStop) {
                g2.setColor(new java.awt.Color(0, 100, 255));  // blue - boarding
            } else if (isToStop) {
                g2.setColor(new java.awt.Color(255, 50, 50));  // red - alighting
            } else {
                g2.setColor(new java.awt.Color(100, 100, 100)); // default
            }

            // draw filled circle for stop
            g2.fillOval(centerX - 8, stopY[i] - 8, 16, 16);

            // draw outline
            g2.setColor(java.awt.Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.drawOval(centerX - 8, stopY[i] - 8, 16, 16);

            // stop name label
            g2.setColor(java.awt.Color.BLACK);
            g2.setFont(new java.awt.Font("Arial",
                    isCurrent || isFromStop || isToStop
                            ? java.awt.Font.BOLD : java.awt.Font.PLAIN, 12));
            g2.drawString(name, centerX + 20, stopY[i] + 5);

            // tag labels
            if (isFromStop) {
                g2.setColor(new java.awt.Color(0, 100, 255));
                g2.drawString("◄ Board", centerX - 80, stopY[i] + 5);
            }
            if (isToStop) {
                g2.setColor(new java.awt.Color(255, 50, 50));
                g2.drawString("◄ Alight", centerX - 80, stopY[i] + 5);
            }
        }

        // draw live bus dot between current and next stop
        if (currentStopIndex >= 0 && nextStopIndex >= 0 && progressPct > 0) {

            double fraction = progressPct / 100.0;
            int dotY = (int) (stopY[currentStopIndex]
                    + fraction * (stopY[nextStopIndex] - stopY[currentStopIndex]));

            // bus dot shadow
            g2.setColor(new java.awt.Color(0, 0, 0, 60));
            g2.fillOval(centerX - 9, dotY - 9, 20, 20);

            // bus dot
            g2.setColor(new java.awt.Color(255, 50, 50));
            g2.fillOval(centerX - 8, dotY - 8, 18, 18);

            // bus icon text
            g2.setColor(java.awt.Color.WHITE);
            g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 9));
            g2.drawString("BUS", centerX - 7, dotY + 4);
        }
    }

    // call this when frame closes to stop the timer
    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        if (liveTimer != null) {
            liveTimer.stop();
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

        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        mapPanel = new javax.swing.JPanel(){

            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                drawRoute(g);
            }
        };
        to_input = new javax.swing.JLabel();
        from_input = new javax.swing.JLabel();
        current = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        BG = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setText("BUS-LOCATION");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 10, -1, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel1.setText("FROM");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 90, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel3.setText("TO");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 70, -1, 40));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel5.setText("MAP");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 230, -1, -1));

        mapPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51), 5)));

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 432, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 332, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(mapPanel);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 190, 450, 350));

        to_input.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        to_input.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(to_input, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 70, 220, 30));

        from_input.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        from_input.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(from_input, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 90, 130, 30));

        current.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        current.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(current, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 140, 320, 50));

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 51, 51));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bus_route_booking/images/back-button.png"))); // NOI18N
        jButton2.addActionListener(this::jButton2ActionPerformed);
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        BG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/user/routes (1).jpg"))); // NOI18N
        BG.setPreferredSize(new java.awt.Dimension(1280, 560));
        getContentPane().add(BG, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
         user_local frame=new user_local();
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
                java.awt.EventQueue.invokeLater(() -> new bus_route().setVisible(true));
            }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BG;
    private javax.swing.JLabel current;
    private javax.swing.JLabel from_input;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel mapPanel;
    private javax.swing.JLabel to_input;
    // End of variables declaration//GEN-END:variables
        }
                
