package gatewayserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class TestGatewayServerUDP {
    public static void main(String[] args) throws IOException, SocketException {
        DatagramSocket client = new DatagramSocket();
        InetAddress address = InetAddress.getByName("localhost");
        byte[] buffer;
        DatagramPacket packet;
        int PORT = 12346;

        buffer = "RegCompany&companyName@Apple".getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);

        buffer = "RegCompany&companyName@Microsoft".getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);

        buffer = "RegCompany&companyName@Sony".getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);

        //System.out.println("\n Products:: ");
        buffer = "RegProduct&productName@iphone".getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);

        buffer = "RegProduct&productName@xbox".getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);

        buffer = "RegProduct&productName@smartTV".getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);

        //System.out.println("\n Devices: ");

        buffer = "RegDevice&deviceName@Tzur's_iphone#deviceOwner@TzurB".getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);

        buffer = "RegDevice&deviceName@Elad's_pc#deviceOwner@EladZ".getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);

        buffer = "RegDevice&deviceName@Simon's_smartWatch#deviceOwner@SimonG".getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);

        //System.out.println("\n Updates: ");
        buffer = "RegUpdate&update@charge_battery".getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);

        buffer = "RegUpdate&update@gas_low".getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);

        buffer = "RegUpdate&update@new_update_available".getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        client.send(packet);
    }
}
