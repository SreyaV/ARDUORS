package com.annaleonard.myfirstimmersion;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.IllegalBlockingModeException;
import java.text.DecimalFormat;

/**
 * Created by gglass on 6/15/16.
 */
public class RunJointData implements Runnable {
    DatagramSocket socket;
    DatagramPacket packet;
    final String[] jointStringArray = new String[7];
    DecimalFormat jointPosFormat = new DecimalFormat("0.00");   //format to specify sig figs
    double[] jointDoubleArray = new double[7];
    boolean receivingData;



    RunJointData(){
    }


    public void start() {
       run();
    }

    public double[] getJointDoubles(){
        return jointDoubleArray;
    }
    public String[] getJointStringArray() {return jointStringArray;}
    public boolean getReceivingData(){return receivingData;}


    public void run(){
//        Thread thisThread = Thread.currentThread(); //set flag to current thread
        Log.i("RunJointData", "running");
        int count = 0;
            //check that the socket does not exist already before creating and binding it
        if (socket == null) {
            try {
                socket = new DatagramSocket(61557, InetAddress.getByName("10.0.0.15")); //Use Glass IP address here
                Log.i("RunJointData", "socket created");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }



        while (true) {

            Log.i("RunJointData", "while loop running " + count);
            count++;


            byte[] buf = new byte[56];
            packet = new DatagramPacket(buf, buf.length);
            Log.i("RunJointData", "packet shell created");

            try {
                Thread.sleep(10, 0);

                try {
                    Log.i("RunJointData", "try entered");
                    socket.receive(packet);   //receive UDP packet
                    receivingData = true;
                    Log.i("RunJointData", "packet received");

                } catch (NullPointerException e) {
                    receivingData = false;
                    e.printStackTrace();
                    Log.i("RunJointData", "null exc on receive");
                } catch (PortUnreachableException e) {
                    receivingData = false;
                    e.printStackTrace();
                    Log.i("RunJointData", "port unreachable exception");

                } catch (IOException e) {
                    receivingData = false;
                    e.printStackTrace();
                    Log.i("RunJointData", "IO Exception");
                } catch (IllegalBlockingModeException e) {
                    receivingData = false;
                    e.printStackTrace();
                    Log.i("RunJointData", "blocking mode exception");
                }
//

                //Get data from UDP packet and convert to user-ready information    (joint values are in degrees)
                if (receivingData) {
                    for (int i = 0; i < 7; i++) {
                        jointDoubleArray[i] = ByteBuffer.wrap(packet.getData()).order(ByteOrder.LITTLE_ENDIAN).getDouble(i * 8);
                        jointStringArray[i] = String.valueOf(jointPosFormat.format(jointDoubleArray[i]));
                    }
                    Log.i("RunJointData", "arrays created");
                }


                //                Log.i("IOException",e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }

    }
