package com.mycompany.desktop.models;

import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author admin
 */
public class CombinedResultWrapper {
    private List<Integer> max_appointments;
    private List<Integer> appointment_counts;

    public List<Integer> getMax_appointments() {
        return max_appointments;
    }

    public void setMax_appointments(List<Integer> max_appointments) {
        this.max_appointments = max_appointments;
    }

    public List<Integer> getAppointment_counts() {
        return appointment_counts;
    }

    public void setAppointment_counts(List<Integer> appointment_counts) {
        this.appointment_counts = appointment_counts;
    }
}
