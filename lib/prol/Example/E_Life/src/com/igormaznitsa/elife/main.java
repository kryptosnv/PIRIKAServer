package com.igormaznitsa.elife;

import javax.swing.SwingUtilities;

public class main {

    public static final void main(String... args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MainForm();
            }
        });
    }
}
