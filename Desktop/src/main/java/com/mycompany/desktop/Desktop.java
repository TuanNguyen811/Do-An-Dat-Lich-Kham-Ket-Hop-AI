/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.desktop;

/**
 *
 * @author admin
 */
public class Desktop {
    
    public static void main(String[] args) {
        try {
            // Đặt Look and Feel Nimbus (nếu có)
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Nếu Nimbus không có hoặc lỗi, fallback Look and Feel mặc định
            e.printStackTrace();
        }

        // Khởi tạo và hiện Login form
        java.awt.EventQueue.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}