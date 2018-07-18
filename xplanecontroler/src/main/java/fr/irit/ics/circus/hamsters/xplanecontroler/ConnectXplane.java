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
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author barboni
 */
public class ConnectXplane {

    private final int xplanePort;
    private final InetAddress xplaneAddr;
    private final DatagramSocket socket;
    private final DatagramSocket listenersocket;

    public ConnectXplane() throws SocketException, UnknownHostException, IOException {
        this.socket = new MulticastSocket(0);
        this.xplaneAddr = InetAddress.getByAddress(new byte[]{(byte) 141, (byte) 115, (byte) 66, (byte) 26});
        //      this.xplaneAddr = InetAddress.getByAddress(new byte[]{(byte) 141, (byte) 115, (byte) 38, (byte) 155});
        this.xplanePort = 49000;
        this.socket.setSoTimeout(1000);  // this.socket.set
        this.socket.setReuseAddress(true);
        this.listenersocket = new MulticastSocket(49001);
        //   listenersocket.accept();
        //    this.socket = new DatagramSocket(0);
        /* new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    byte[] buffer = new byte[65536];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    try {
                        System.err.println("dsd");
                        listenersocket.receive(packet);
                        // System.err.println(packet.getLength());
                        Arrays.copyOf(buffer, packet.getLength());
                        for (int i = 0; i < packet.getLength(); i++) {
                            System.err.print(Character.toString((char) buffer[i]));
                        }
                        System.err.println("");
                        //  System.err.println(Character.toString((char) buffer[0]) + " " + Character.toString((char) buffer[1]) + " " +Character.toString((char) buffer[2]));
                    } catch (SocketTimeoutException ex) {
                        System.err.println("timeout" + ex.getMessage());
                    } catch (IOException ex) {
                        Logger.getLogger(ConnectXplane.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        })
                .start();*/
    }

    private void sendUDP(byte[] buffer) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, xplaneAddr, xplanePort);
        socket.send(packet);
    }

    /*
    private byte[] readUDP() throws IOException {
        byte[] buffer = new byte[65536];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            return Arrays.copyOf(buffer, packet.getLength());
        } catch (SocketTimeoutException ex) {
            return new byte[0];
        }
    }
     */
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

        // System.err.println(os.size());
        sendUDP(os.toByteArray());
        try {
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectXplane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    static int ID;

    public int getDREFs(String drefs, int freqHZ) throws IOException {
        //Preconditions
        /* if (drefs == null || drefs.length == 0) {
            throw new IllegalArgumentException(("drefs must be non-empty."));
        }*/
 /*if (values == null || values.length != drefs.length) {
            throw new IllegalArgumentException("values must be of the same size as drefs.");
        }*/
        int myiD = ID++;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write("RREF".getBytes(StandardCharsets.UTF_8));
        os.write(0x00); //Placeholder for message length
        // freq
        os.write(0x01);
        os.write(0x00);
        os.write(0x00);
        os.write(0x00);
//id
        os.write(0x05);
        os.write(0x00);
        os.write(0x00);
        os.write(0x00);
        //   os.write(myiD);
        String dref = drefs;


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

        //       ByteBuffer bb = ByteBuffer.allocate(4 * 1);
        //   bb.order(ByteOrder.LITTLE_ENDIAN);
        //for (int j = 0; j < value.length; ++j) {
        // bb.putFloat(value);
        //   }
        //Build and send message
        //  os.write(value.length);
        // os.write(bb.array());
        ///   os.write(drefBytes.length);
        os.write(drefBytes, 0, drefBytes.length);
        os.write(0);
        os.writeTo(System.out);
        /*   int size = os.size();
        for (int i = size; i < 509; i++) {
            os.write(0x20);
        }*/
        int size = os.size();
        for (int i = size; i < 509; i++) {
            os.write(0x20);
        }

        sendUDP(os.toByteArray());
        try {
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectXplane.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*
        byte[] data = readUDP();
        if (data.length == 0) {
            throw new IOException("No response received.");
        }
        if (data.length < 6) {
            throw new IOException("Response too short");
        }
        ByteBuffer rbb = ByteBuffer.wrap(data);
        rbb.order(ByteOrder.LITTLE_ENDIAN);
        float aFloat = rbb.getFloat();
         */
        return myiD;
        /**/
    }

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

        sendDREF("sim/operation/override/override_annunciators", value);
        Thread.sleep(20);

        /*sendDREF("sim/cockpit/warnings/annunciator_test_pressed", value);
        Thread.sleep(20);*/
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
        /* sendDREF("sim/flightmodel/failures/stallwarning", value);
        Thread.sleep(20);*/

    }

    void removePAAlt() throws IOException, InterruptedException {
        System.err.println("sending low to autopilot");
        sendDREF("sim/cockpit/autopilot/autopilot_mode", 0f);
        Thread.sleep(20);

        sendDREF("sim/cockpit/autopilot/altitude", 2000.0f);
        Thread.sleep(20);
        sendDREF("sim/cockpit/radios/com1_freq_hz", 12330.0f);
        Thread.sleep(20);
        sendDREF("sim/cockpit/radios/com1_freq_hz", 12330.0f);
        Thread.sleep(20);

        sendDREF("sim/cockpit/radios/com1_freq_hz", 12430.0f);
        Thread.sleep(20);
        float value = 0;

        /*sendDREF("sim/cockpit/warnings/annunciator_test_pressed", value);
        Thread.sleep(20);*/
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

        sendDREF("sim/operation/override/override_annunciators", value);
        Thread.sleep(20);
        /*   sendDREF("sim/flightmodel/failures/stallwarning", value);
        Thread.sleep(20);*/
    }

    void masterCaution(boolean cc) throws IOException {

        float value = (cc) ? 1.0f : 0.0f;
        sendDREF("sim/operation/override/override_annunciators", 1.0f);
        sendDREF("sim/cockpit/warnings/master_caution_on", value);
        sendDREF("sim/cockpit/warnings/annunciators/master_caution", value);

    }

    void masterWarning(boolean cc) throws IOException {

        float value = (cc) ? 1.0f : 0.0f;
        sendDREF("sim/operation/override/override_annunciators", 1.0f);
        sendDREF("sim/cockpit/warnings/master_warning_on", value);
        sendDREF("sim/cockpit/warnings/annunciators/master_warning", value);

    }

    float getAPAlt() throws IOException {
        int id = getDREFs("sim/cockpit/autopilot/altitude", 3);
        return 0f;
    }
}
