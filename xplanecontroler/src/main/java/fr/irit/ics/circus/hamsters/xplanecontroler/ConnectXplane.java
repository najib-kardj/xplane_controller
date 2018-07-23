/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.irit.ics.circus.hamsters.xplanecontroler;

import fr.irit.ics.circus.hamsters.xplanecontroler.events.DataRefChangedEvent;
import fr.irit.ics.circus.hamsters.xplanecontroler.events.DataRefChangedListener;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author barboni
 */
public class ConnectXplane {

    private final int xplaneSendingPort = 49000;
    private final int xplaneReceivingPort = 49500;
    private final InetAddress xplaneAddr = InetAddress.getByAddress(new byte[]{(byte) 141, (byte) 115, (byte) 66, (byte) 26});
    private final DatagramSocket socket;

    private Map<Integer, Pair<String, Float>> subscribedDataRef = new HashMap<>();

    private final List<DataRefChangedListener> listeners = new ArrayList<>();

    public ConnectXplane() throws SocketException, UnknownHostException, IOException {
        this.socket = new DatagramSocket(xplaneReceivingPort);
        new Thread(() -> {
            while (true) {
                receive();
            }
        }).start();
    }

    private void receive() {
        byte[] receive = new byte[65535];
        DatagramPacket recu = new DatagramPacket(receive, receive.length);
        try {
            socket.receive(recu);
            int i = 5;
            while (i < recu.getLength()) {
                int receivedID = (int) receive[i] + 256 * (int) receive[i + 1] + 65536 * (int) receive[i + 2] + 16777216 * (int) receive[i + 3];
                byte[] wrappedValue = new byte[]{receive[i + 4], receive[i + 5], receive[i + 6], receive[i + 7]};
                float theValue = ByteBuffer.wrap(wrappedValue).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                if (!subscribedDataRef.containsKey(receivedID)) {
                    System.err.println("Invalid ID: " + receivedID);
                } else {
                    subscribedDataRef.get(receivedID).setSecond(theValue);
                    DataRefChangedEvent event = new DataRefChangedEvent(this, receivedID, subscribedDataRef.get(receivedID).getFirst(), subscribedDataRef.get(receivedID).getSecond());
                    for (DataRefChangedListener listener : listeners) {
                        listener.dataRefChanged(event);
                    }
                }
                //System.err.println("ID [" + received + "] Value = " + theValue);
                i += 8;
            }
        } catch (IOException ex) {
            Logger.getLogger(ConnectXplane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendDataRef(String dataRef, float values) throws IOException {
        //Preconditions
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String prolog = "DREF" + ((char) 0);
        os.write(prolog.getBytes(), 0, 5);
        float value = values;
        //Convert drefs to bytes.
        ByteBuffer bbValue = ByteBuffer.allocate(4 * 1);
        bbValue.order(ByteOrder.LITTLE_ENDIAN);
        bbValue.putFloat(value);
        // write float to array
        os.write(bbValue.array());
        // write dataregf
        os.write(dataRef.getBytes(), 0, dataRef.getBytes().length);
        for (int k = 0; k < 500 - dataRef.length(); k++) {
            os.write((char) 0);
        }
        try {
            sendUDP(os.toByteArray());
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectXplane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendDATA(int id, float[] f) throws IOException {
        assert f.length == 8;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String prolog = "DATA" + ((char) 0);
        os.write(prolog.getBytes(), 0, 5);
        ByteBuffer bbindex = ByteBuffer.allocate(4 * 1);
        bbindex.order(ByteOrder.LITTLE_ENDIAN);
        bbindex.putInt(id);
        // write float to array
        os.write(bbindex.array());
        for (float ff : f) {
            ByteBuffer bbValue = ByteBuffer.allocate(4 * 1);
            bbValue.order(ByteOrder.LITTLE_ENDIAN);
            bbValue.putFloat(ff);
            // write float to array
            os.write(bbValue.array());
        }
        try {
            sendUDP(os.toByteArray());
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectXplane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendCMD(String simflight_controls) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String prolog = "CMND" + ((char) 0);
        os.write(prolog.getBytes(), 0, 5);
        os.write(simflight_controls.getBytes(), 0, simflight_controls.getBytes().length);
        os.write((char) 0);
        try {
            sendUDP(os.toByteArray());
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectXplane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendOBJN(String resourceslocation, int id) throws IOException {
        //Preconditions
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String prolog = "OBJN" + ((char) 0);
        os.write(prolog.getBytes(), 0, 5);
        int value = id;
        //Convert drefs to bytes.
        ByteBuffer bbValue = ByteBuffer.allocate(4 * 1);
        bbValue.order(ByteOrder.LITTLE_ENDIAN);
        bbValue.putInt(value);
        // write float to array
        os.write(bbValue.array());
        // write dataregf
        os.write(resourceslocation.getBytes(), 0, resourceslocation.getBytes().length);
        for (int k = 0; k < 500 - resourceslocation.length(); k++) {
            os.write((char) 0);
        }
        try {
            sendUDP(os.toByteArray());
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectXplane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendOBJL(int idobject, double[] latlongelev, float[] phithespi, int onground, float smoke) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String prolog = "OBJL" + ((char) 0);
        os.write(prolog.getBytes(), 0, 5);
        int value = idobject;
        //Convert drefs to bytes.
        ByteBuffer bbValue = ByteBuffer.allocate(4 * 1);
        bbValue.order(ByteOrder.LITTLE_ENDIAN);
        bbValue.putInt(value);
        // write float to array
        os.write(bbValue.array());
        // double dataregf
        /* align message */
        ByteBuffer padding = ByteBuffer.allocate(4 * 1);
        padding.order(ByteOrder.LITTLE_ENDIAN);
        padding.putFloat(0);
        os.write(padding.array());
        for (double f : latlongelev) {
            ByteBuffer vv = ByteBuffer.allocate(8 * 1);
            vv.order(ByteOrder.LITTLE_ENDIAN);
            vv.putDouble(f);
            // write float to array
            os.write(vv.array());
        }
        int i = 0;

        for (float f : phithespi) {
            ByteBuffer vv = ByteBuffer.allocate(4 * 1);
            vv.order(ByteOrder.LITTLE_ENDIAN);
            vv.putFloat(f);
            // write float to array
            os.write(vv.array());
            if (i < 2) {

            }
            i++;
        }

        ByteBuffer bbonground = ByteBuffer.allocate(4 * 1);
        bbonground.order(ByteOrder.LITTLE_ENDIAN);
        bbonground.putInt(onground);
        // write float to array
        os.write(bbonground.array());
        /* align message */
        ByteBuffer padding2 = ByteBuffer.allocate(4 * 1);
        padding2.order(ByteOrder.LITTLE_ENDIAN);
        padding2.putFloat(0);
        os.write(padding2.array());
        ByteBuffer bbsmoke = ByteBuffer.allocate(4 * 1);
        bbsmoke.order(ByteOrder.LITTLE_ENDIAN);
        bbsmoke.putFloat(smoke);
        // write float to array
        os.write(bbsmoke.array());

        System.err.println("os.toByteArray()" + os.toByteArray().length);
        /* os.write(0);
        os.write(0);
        os.write(0);
        os.write(0);
        os.write(0);
        os.write(0);
        os.write(0);
        os.write(0);
         */
        try {
            sendUDP(os.toByteArray());
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectXplane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void subscribeToDataRef(int idref, String dataRef, int frequency) throws IOException {
        if (subscribedDataRef.containsKey(idref)) {
            throw new IllegalStateException("ID " + idref + " already in use for DataRef " + subscribedDataRef.get(idref).getFirst() + ".");
        } else {
            subscribedDataRef.put(idref, new Pair<>(dataRef, 0.f));
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String prolog = "RREF" + ((char) 0);
        os.write(prolog.getBytes(), 0, 5);
        ByteBuffer bbRef = ByteBuffer.allocate(4 * 1);
        bbRef.order(ByteOrder.LITTLE_ENDIAN);
        bbRef.putInt(frequency);
        os.write(bbRef.array());
        ByteBuffer bbID = ByteBuffer.allocate(4 * 1);
        bbID.order(ByteOrder.LITTLE_ENDIAN);
        bbID.putInt(idref);
        os.write(bbID.array());
        os.write(dataRef.getBytes(), 0, dataRef.getBytes().length);
        for (int k = 0; k < 400 - dataRef.length(); k++) {
            os.write((char) 0);
        }
        try {
            sendUDP(os.toByteArray());
            Thread.sleep(20);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ConnectXplane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendUDP(byte[] buffer) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, xplaneAddr, xplaneSendingPort);
        socket.send(packet);
    }

    void addPAAlt() throws IOException, InterruptedException {
        System.err.println("sending alt to autopilot");

        sendDataRef("sim/cockpit/autopilot/altitude", 2000.0f);

        sendDataRef("sim/cockpit/radios/com1_freq_hz", 12330.0f);
        sendDataRef("sim/cockpit/radios/com2_freq_hz", 12330.0f);

        sendDataRef("sim/cockpit/autopilot/autopilot_mode", 2f);
        float value = 1;

        sendDataRef("sim/operation/override/override_annunciators", value);

        /*sendDREF("sim/cockpit/warnings/annunciator_test_pressed", value);
        Thread.sleep(20);*/
        sendDataRef("sim/cockpit/warnings/master_caution_on", value);
        sendDataRef("sim/cockpit/warnings/master_warning_on", value);
        sendDataRef("sim/cockpit/warnings/annunciators/master_caution", value);
        sendDataRef("sim/cockpit/warnings/annunciators/master_warning", value);
        sendDataRef("sim/cockpit2/annunciators/master_caution", value);
        sendDataRef("sim/cockpit2/annunciators/master_warning", value);

    }

    void removePAAlt() throws IOException, InterruptedException {
        System.err.println("sending low to autopilot");
        sendDataRef("sim/cockpit/autopilot/autopilot_mode", 0f);

        sendDataRef("sim/cockpit/autopilot/altitude", 0.0f);
        sendDataRef("sim/cockpit/radios/com1_freq_hz", 12330.0f);
        sendDataRef("sim/cockpit/radios/com1_freq_hz", 12330.0f);

        sendDataRef("sim/cockpit/radios/com1_freq_hz", 12430.0f);
        float value = 0;

        sendDataRef("sim/cockpit/warnings/master_caution_on", value);
        sendDataRef("sim/cockpit/warnings/master_warning_on", value);
        sendDataRef("sim/cockpit/warnings/annunciators/master_caution", value);
        sendDataRef("sim/cockpit/warnings/annunciators/master_warning", value);
        sendDataRef("sim/cockpit2/annunciators/master_caution", value);

        sendDataRef("sim/operation/override/override_annunciators", value);

    }

    void masterCaution(boolean cc) throws IOException {

        float value = (cc) ? 1.0f : 0.0f;
        sendDataRef("sim/operation/override/override_annunciators", 1.0f);
        sendDataRef("sim/cockpit/warnings/master_caution_on", value);
        sendDataRef("sim/cockpit/warnings/annunciators/master_caution", value);

    }

    void masterWarning(boolean cc) throws IOException {

        float value = (cc) ? 1.0f : 0.0f;
        sendDataRef("sim/operation/override/override_annunciators", 1.0f);
        sendDataRef("sim/cockpit/warnings/master_warning_on", value);
        sendDataRef("sim/cockpit/warnings/annunciators/master_warning", value);

    }

    float getAPAlt() throws IOException {
        //subscribeToDataRef((byte) 3, "sim/cockpit/autopilot/altitude", (byte) 1);
        return 0f;
    }

    void localX(String text) throws IOException {
        sendDataRef("sim/flightmodel/position/local_x", Float.parseFloat(text));
    }

    void localY(String text) throws IOException {
        sendDataRef("sim/flightmodel/position/local_y", Float.parseFloat(text));
    }

    void localZ(String text) throws IOException {
        sendDataRef("sim/flightmodel/position/local_z", Float.parseFloat(text));
    }

    void theta(String text) throws IOException {
        sendDataRef("sim/flightmodel/position/theta", Float.parseFloat(text));
    }

    void phi(String text) throws IOException {
        sendDataRef("sim/flightmodel/position/phi", Float.parseFloat(text));
    }

    void psi(String text) throws IOException {
        sendDataRef("sim/flightmodel/position/psi", Float.parseFloat(text));
    }

    void putOil() throws IOException {
        sendOBJN("Resources/default scenery/sim objects/dynamic/OilPlatform.obj", 1);
        sendOBJN("Resources/default scenery/sim objects/dynamic/OilRig.obj", 2);
        //
        sendOBJL(1, new double[]{46.189465d, 6.275749d, 600d}, new float[]{0f, 0f, 0f}, 1, 2f);
        sendOBJL(2, new double[]{46.189465d, 6.275745d, 600d}, new float[]{0f, 0f, 0f}, 1, 2f);
    }

    public void addDataRefChangedEventListener(DataRefChangedListener drcel) {
        listeners.add(drcel);
    }

    public void removeDataRefChangedEventListener(DataRefChangedListener drcel) {
        listeners.remove(drcel);
    }

    void brakes() throws IOException {
// 25 for throttle
        sendDATA(25, new float[]{100, 0, 0, 0, 0, 0, 0, 0});
        sendCMD("sim/flight_controls/brakes_toggle_regular");
    }

}
