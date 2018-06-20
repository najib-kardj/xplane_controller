/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.irit.ics.circus.hamsters.xplanecontroler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author barboni
 */
public class ConnectXplane {

    private final int xplanePort;
    private final InetAddress xplaneAddr;
    private final DatagramSocket socket;

    public ConnectXplane() throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket(0);
        this.xplaneAddr = InetAddress.getByAddress(new byte[]{(byte) 141, (byte) 115, (byte) 66, (byte) 26});
        this.xplanePort = 49000;
        this.socket.setSoTimeout(30);
    }

    private void sendUDP(byte[] buffer) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, xplaneAddr, xplanePort);
        socket.send(packet);
    }

    public void sendDREF(String drefs, float values) throws IOException {
        //Preconditions
        /* if (drefs == null || drefs.length == 0) {
            throw new IllegalArgumentException(("drefs must be non-empty."));
        }*/
 /*if (values == null || values.length != drefs.length) {
            throw new IllegalArgumentException("values must be of the same size as drefs.");
        }*/

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write("DREF".getBytes(StandardCharsets.UTF_8));
        os.write(0x00); //Placeholder for message length

        String dref = drefs;
        float value = values;

        /* if (dref == null) {
            throw new IllegalArgumentException("dref must be a valid string.");
        }
        if (value == null || value.length == 0) {
            throw new IllegalArgumentException("value must be non-null and should contain at least one value.");
        }
         */
        //Convert drefs to bytes.
        byte[] drefBytes = dref.getBytes(StandardCharsets.UTF_8);
        if (drefBytes.length == 0) {
            throw new IllegalArgumentException("DREF is an empty string!");
        }
        if (drefBytes.length > 255) {
            throw new IllegalArgumentException("dref must be less than 255 bytes in UTF-8. Are you sure this is a valid dref?");
        }

        ByteBuffer bb = ByteBuffer.allocate(4 * 1);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        //for (int j = 0; j < value.length; ++j) {
        bb.putFloat(value);
        //   }

        //Build and send message
        //  os.write(value.length);
        os.write(bb.array());
        ///   os.write(drefBytes.length);
        os.write(drefBytes, 0, drefBytes.length);
        os.write(0);

        int size = os.size();
        for (int i = size; i < 509; i++) {
            os.write(0x20);
        }

        System.err.println(os.size());
        sendUDP(os.toByteArray());
    }

    /* public void sendDREF(String drefs, int values) throws IOException {
        //Preconditions
       

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write("DREF".getBytes(StandardCharsets.UTF_8));
        os.write(0x00); //Placeholder for message length

        String dref = drefs;
        float value = values;

       
        //Convert drefs to bytes.
        byte[] drefBytes = dref.getBytes(StandardCharsets.UTF_8);
        if (drefBytes.length == 0) {
            throw new IllegalArgumentException("DREF is an empty string!");
        }
        if (drefBytes.length > 255) {
            throw new IllegalArgumentException("dref must be less than 255 bytes in UTF-8. Are you sure this is a valid dref?");
        }

        ByteBuffer bb = ByteBuffer.allocate(4 * 1);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        //for (int j = 0; j < value.length; ++j) {
        bb.putFloat(value);
        //   }

        //Build and send message
        //  os.write(value.length);
        os.write(bb.array());
        ///   os.write(drefBytes.length);
        os.write(drefBytes, 0, drefBytes.length);
        os.write(0);

        int size = os.size();
        for (int i = size; i < 509; i++) {
            os.write(0x20);
        }

        System.err.println(os.size());
        sendUDP(os.toByteArray());
    }
     */
 /*public void sendDREF(String dref, float value) throws IOException {
        sendDREF(dref, new float[]{value});
    }
     */
    /**
     * Sends a command to X-Plane that sets the given DREF.
     *
     * @param dref The name of the X-Plane dataref to set.
     * @param value An array of floating point values whose structure depends on
     * the dref specified.
     * @throws IOException If the command cannot be sent.
     */
    /*  public void sendDREF(String dref, float[] value) throws IOException {
        sendDREFs(new String[]{dref}, new float[][]{value});
    }
     */
    void addPAAlt() throws IOException, InterruptedException {
        System.err.println("sending alt to autopilot");
        /*"sim/cockpit/autopilot/altitude", 620.0f
         String dref = "sim/cockpit/switches/gear_handle_status";
    float[] value = {0.0F};
         */
        sendDREF("sim/cockpit/autopilot/altitude", 2000.0f);
        Thread.sleep(20);
        sendDREF("sim/cockpit/radios/com1_freq_hz", 12330.0f);
        Thread.sleep(20);
        sendDREF("sim/cockpit/radios/com2_freq_hz", 12330.0f);
        Thread.sleep(20);

        sendDREF("sim/cockpit/autopilot/autopilot_mode", 2f);
        Thread.sleep(20);
        float value = 1;
        sendDREF("sim/cockpit/warnings/annunciator_test_pressed", value);
        Thread.sleep(20);
        sendDREF("sim/cockpit/warnings/master_caution_on", value);
        Thread.sleep(20);
        sendDREF("sim/cockpit/warnings/master_warning_on", value);
        Thread.sleep(20);
        sendDREF("sim/cockpit/warnings/annunciators/master_caution", value);
        Thread.sleep(20);
        sendDREF("sim/cockpit/warnings/annunciators/master_warning", value);
        Thread.sleep(20);
        sendDREF("sim/cockpit2/annunciators/master_caution", value);
        Thread.sleep(20);
        sendDREF("sim/cockpit2/annunciators/master_warning", value);
        Thread.sleep(20);
    }

    void removePAAlt() throws IOException, InterruptedException {
        System.err.println("sending low to autopilot");
        sendDREF("sim/cockpit/autopilot/autopilot_mode", 0f);
        Thread.sleep(20);
        /*"sim/cockpit/autopilot/altitude", 620.0f
         String dref = "sim/cockpit/switches/gear_handle_status";
    float[] value = {0.0F};
        
         */
        sendDREF("sim/cockpit/autopilot/altitude", 2000.0f);
        Thread.sleep(20);
        sendDREF("sim/cockpit/radios/com1_freq_hz", 12330.0f);
        Thread.sleep(20);
        sendDREF("sim/cockpit/radios/com1_freq_hz", 12330.0f);
        Thread.sleep(20);

        sendDREF("sim/cockpit/radios/com1_freq_hz", 12430.0f);
        Thread.sleep(20);
        float value = 0;
        sendDREF("sim/cockpit/warnings/annunciator_test_pressed", value);
        Thread.sleep(20);
        sendDREF("sim/cockpit/warnings/master_caution_on", value);
        Thread.sleep(20);
        sendDREF("sim/cockpit/warnings/master_warning_on", value);
        Thread.sleep(20);
        sendDREF("sim/cockpit/warnings/annunciators/master_caution", value);
        Thread.sleep(20);
        sendDREF("sim/cockpit/warnings/annunciators/master_warning", value);
        Thread.sleep(20);

        //sendDREF("sim/cockpit2/annunciators/master_caution", value);
        //   sendDREF("sim/cockpit2/annunciators/master_warning", value);
    }
}
