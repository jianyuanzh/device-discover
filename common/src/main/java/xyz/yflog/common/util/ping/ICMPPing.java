package xyz.yflog.common.util.ping;

import org.savarese.rocksaw.net.RawSocket;
import org.savarese.vserv.tcpip.ICMPEchoPacket;
import org.savarese.vserv.tcpip.ICMPPacket;
import org.savarese.vserv.tcpip.OctetConverter;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by vincent on 15/6/7.
 */
public class ICMPPing implements IPing {
    private int timeout;  // in seconds;
    private RawSocket socket;

    public ICMPPing() {
        this(5);
    }

    public ICMPPing(int timeout) {
        this.timeout = timeout;
    }

    public PingResult ping(InetAddress host, int count) throws IOException {
        return null;
    }

    public PingResult ping(String hostname, int count) throws IOException {
        return ping(InetAddress.getByName(hostname), count);
    }

    public void close() throws IOException {

    }

    private ICMPEchoPacket preparePacket(byte[] data, int seq) {
        ICMPEchoPacket packet = new ICMPEchoPacket(1);

        packet.setData(data);
        packet.setIPHeaderLength(5);
        packet.setICMPDataByteLength(56);
        packet.setType(ICMPPacket.TYPE_ECHO_REQUEST);
        packet.setCode(0);
        packet.setSequenceNumber(seq);
        packet.setIdentifier(hashCode() & 0xFFFF);

        int dataOffset = packet.getIPHeaderByteLength() + packet.getICMPHeaderByteLength();
        OctetConverter.longToOctets(System.currentTimeMillis(), data, dataOffset);
        packet.computeICMPChecksum();
        return packet;
    }

    private long sendPacket(InetAddress host, int seq) throws IOException {
        if (socket == null) ;
//            throw new Throwable("RawSocket is not initialized .");
        byte[] data = new byte[84];
        ICMPEchoPacket packet = preparePacket(data, seq);
        socket.write(host, data, packet.getIPHeaderByteLength(), packet.getICMPPacketByteLength());
        try {

            do {
                socket.read(host, data);
            } while (packet.getType() != ICMPPacket.TYPE_ECHO_REPLY ||
                    packet.getIdentifier() != (hashCode() & 0xFFFF) ||
                    packet.getSequenceNumber() != seq);

            if (packet.getSourceAsInetAddress().equals(host)) {
                int dataOffset = packet.getIPHeaderByteLength() + packet.getICMPHeaderByteLength();
                long end = System.currentTimeMillis();
                long start = OctetConverter.octetsToLong(data, dataOffset);
                long time = end - start;
                return time;
            }
        } catch (UnknownHostException e) {
            System.err.println("Cannot retrieve the source address of an ICMP packet");
            e.printStackTrace();
        } catch (InterruptedIOException e) {
            System.err.println("Receive timeout");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Unable to read from the socket");
            e.printStackTrace();
        }


        return -1L;
    }

    private RawSocket initRawSocket() throws IOException {
        RawSocket socket = new RawSocket();
        socket.open(RawSocket.PF_INET, RawSocket.getProtocolByName("icmp"));
        int mSecTimout = timeout * 1000;
        try {
            socket.setSendTimeout(mSecTimout);
            socket.setReceiveTimeout(mSecTimout);
        } catch (SocketException e) {
            e.printStackTrace();
            socket.setUseSelectTimeout(true);
            socket.setSendTimeout(mSecTimout);
            socket.setReceiveTimeout(mSecTimout);
        }
        return socket;
    }
}
