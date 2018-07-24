/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.irit.ics.circus.hamsters.xplanecontroler;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SYSTEM
 */
public class RadarMap extends javax.swing.JPanel {

    /*wxr_colors[0] = new Color(0,0,0);
        	wxr_colors[1] = new Color(0,5,0);
        	wxr_colors[2] = new Color(0,40,0);
        	wxr_colors[3] = new Color(0,100,0);
        	wxr_colors[4] = new Color(0,120,0);
        	wxr_colors[5] = new Color(120,120,0);
        	wxr_colors[6] = new Color(140,140,0);
        	wxr_colors[7] = new Color(140,0,0);
        	wxr_colors[8] = new Color(160,0,0);
        	wxr_colors[9] = new Color(160,0,160);       */
    private PlanePosition planepos;
    private Color[] WXRColor = new Color[]{
        new Color(0, 0, 0),
        new Color(0, 5, 0),
        new Color(0, 40, 0),
        new Color(0, 100, 0),
        new Color(0, 120, 0),
        new Color(120, 120, 0),
        new Color(140, 140, 0),
        new Color(140, 0, 0),
        new Color(160, 0, 0),
        new Color(160, 0, 160)
    };

    /**
     * Creates new form RadarMap
     */
    public RadarMap() {
        initComponents();
    }
    private List<Info> infos = new ArrayList<>();

    void addPlot(double lat, double lon, float stormheight) {
        synchronized (infos) {
            infos.add(new Info(lat, lon, stormheight));
        }
        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
private float scale = 1f;

    @Override
    public void paint(Graphics g) {
        if (planepos == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        float xc = getWidth() / 2;
        float yy = getHeight() / 2;
        g2.setPaint(Color.BLACK);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());

        g2.transform(AffineTransform.getRotateInstance(
                Math.toRadians(planepos.getHeading()),
                xc,
                yy)
        );

        synchronized (infos) {
            for (Info info : infos) {
                //  System.err.println("ssss");
                float stor = info.getStormHeight();
                info.setPlanePos(planepos);
                info.setMapCenter(xc, yy);
                info.compute();
                //      float bearing = info.getBearing(planepos);
                //double toRadians = Math.toRadians(bearing - 90);
                float x = info.getX();//xc + distance * (float) Math.cos(toRadians);
                float y = info.getY();//yy + distance * (float) Math.sin(toRadians);
                //System.err.println(""+x);
                // System.err.println(""+y
                //);
                int round = Math.round(stor / 11.f);
                if (round > 1) {
                    g2.setPaint(WXRColor[round]);
                    g2.fillRect((int) x, (int) y, (int) Info.pixels_per_nm, (int) Info.pixels_per_nm);
                }
                /*if (stor < 0.1) {

                    //  g2.setPaint(new Color(min, 1.0f, 0f));
                } else if (stor < 25) {
                    g2.setPaint(Color.GREEN);
                    g2.fillRect((int) x, (int) y, 2, 2);
                } else if (stor < 50) {
                    g2.setPaint(Color.YELLOW);
                    g2.fillRect((int) x, (int) y, 2, 2);
                } else if (stor < 75) {
                    g2.setPaint(Color.ORANGE);
                    g2.fillRect((int) x, (int) y, 2, 2);
                } else {
                    g2.setPaint(Color.RED);
                    g2.fillRect((int) x, (int) y, 2, 2);
                }*/
                //  float min = Math.abs(Math.min((int) stor / 100f, 1.0f));
                //   System.err.println("min" + min + ",+" + (int) stor);
                //  g2.setPaint(new Color(min, 1.0f, 0f));
                // g2.fillRect((int) x, (int) y, 1, 1);
                // System.err.println(x + "," + y);
            }
        }
        g2.setPaint(Color.YELLOW);
        g2.fillRect(this.getWidth() / 2 - 2, this.getHeight() / 2 - 2, 4, 4);
    }

    void setPlanePosition(PlanePosition planeposition) {
        this.planepos = planeposition;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}