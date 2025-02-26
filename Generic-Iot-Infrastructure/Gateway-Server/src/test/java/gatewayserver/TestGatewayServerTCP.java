package gatewayserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static java.lang.Thread.sleep;

public class TestGatewayServerTCP {
    public static void main(String[] args) throws IOException, InterruptedException {
        SocketChannel client = SocketChannel.open();
        client.connect(new InetSocketAddress("localhost", 12345));
        client.configureBlocking(false);
        ByteBuffer buffer;

        {
            //System.out.println("\n Companys: ");

            buffer = ByteBuffer.wrap("RegCompany&companyName@Apple".getBytes());
            client.write(buffer);
            buffer.clear();
            sleep(1000);

            buffer = ByteBuffer.wrap("RegCompany&companyName@Microsoft".getBytes());
            client.write(buffer);
            buffer.clear();
            sleep(1000);

            buffer = ByteBuffer.wrap("RegCompany&companyName@Sony".getBytes());
            client.write(buffer);
            buffer.clear();
            sleep(1000);
        }

        {
            //System.out.println("\n Products:: ");

            buffer = ByteBuffer.wrap("RegProduct&productName@iphone".getBytes());
            client.write(buffer);
            buffer.clear();
            sleep(1000);

            buffer = ByteBuffer.wrap("RegProduct&productName@xbox".getBytes());
            client.write(buffer);
            buffer.clear();
            sleep(1000);

            buffer = ByteBuffer.wrap("RegProduct&productName@smartTV".getBytes());
            client.write(buffer);
            buffer.clear();
            sleep(1000);
        }

        {
            //System.out.println("\n Devices: ");

            buffer = ByteBuffer.wrap("RegDevice&deviceName@Tzur's_iphone#deviceOwner@TzurB".getBytes());
            client.write(buffer);
            buffer.clear();
            sleep(1000);

            buffer = ByteBuffer.wrap("RegDevice&deviceName@Elad's_pc#deviceOwner@EladZ".getBytes());
            client.write(buffer);
            buffer.clear();
            sleep(1000);

            buffer = ByteBuffer.wrap("RegDevice&deviceName@Simon's_smartWatch#deviceOwner@SimonG".getBytes());
            client.write(buffer);
            buffer.clear();
            sleep(1000);
        }

        {
            //System.out.println("\n Updates: ");

            buffer = ByteBuffer.wrap("RegUpdate&update@charge_battery".getBytes());
            client.write(buffer);
            buffer.clear();
            sleep(1000);

            buffer = ByteBuffer.wrap("RegUpdate&update@gas_low".getBytes());
            client.write(buffer);
            buffer.clear();
            sleep(1000);

            buffer = ByteBuffer.wrap("RegUpdate&update@new_update_available".getBytes());
            client.write(buffer);
            buffer.clear();
            sleep(1000);
        }

        client.close();
    }

}

