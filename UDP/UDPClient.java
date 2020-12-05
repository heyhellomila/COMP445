package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Random;

public class UDPClient {

    private DatagramSocket clientSocket;

    private InetAddress clientAddress;
    private InetAddress router;
    private short routerPort;

    private InetAddress serverAddress;
    private int serverPort;

    public Random random;

    public void handshake(InetAddress clientAddress, int port) throws IOException {

        Packet syn_packet = new Packet.Builder()
                .setType(Packet.SYN)
                .setSequenceNumber(0)
                .setPeerAddress(clientAddress)
                .setPortNumber(port)
                .setPayload(new byte[Packet.MAX_LEN])
                .create();

        DatagramPacket datagramPacket = new DatagramPacket(syn_packet.toBytes(), syn_packet.toBytes().length, router, routerPort);

        //Implement send SYN
        clientSocket.send(datagramPacket);
        //TODO: implement timeout functionality

        //Receive SYN+ACK
        clientSocket.receive(datagramPacket);
        //TODO: timer thing here

        Packet syn_packet_received = Packet.fromBytes(ByteBuffer.wrap(datagramPacket.getData()));
        System.out.println("received step 2: " + syn_packet_received.toString());

        if (syn_packet_received.getType() == Packet.SYN_ACK) {
            String payload = new String(s);
        }

        serverAddress = syn_packet.getPeerAddress();
        serverPort = syn_packet.getPeerPort();


    }


}
