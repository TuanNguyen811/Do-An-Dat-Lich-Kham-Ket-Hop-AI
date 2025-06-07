/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.desktop.models;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimePickerDialog extends JDialog {
    private DatePicker datePicker;
    private TimePicker timePicker;
    private LocalDateTime selectedDateTime = null;

    public DateTimePickerDialog(Frame parent) {
        super(parent, "Chọn ngày và giờ", true);
        setLayout(new FlowLayout());

        datePicker = new DatePicker();
        timePicker = new TimePicker();

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            LocalDate date = datePicker.getDate();
            LocalTime time = timePicker.getTime();
            if (date != null && time != null) {
                selectedDateTime = LocalDateTime.of(date, time);
                dispose(); // đóng dialog
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ ngày và giờ!");
            }
        });

        add(new JLabel("Ngày:"));
        add(datePicker);

        add(new JLabel("Giờ:"));
        add(timePicker);

        add(okButton);

        setSize(300, 150);
        setLocationRelativeTo(parent);
    }

    // Hàm tĩnh để mở dialog và trả về thời gian đã chọn
    public static LocalDateTime showDialog(Component parent) {
        Frame frame = JOptionPane.getFrameForComponent(parent);
        DateTimePickerDialog dialog = new DateTimePickerDialog(frame);
        dialog.setVisible(true);
        return dialog.selectedDateTime;
    }
}