package org.gregoire;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.net.InetSocketAddress;

public class MinaDualSocketServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        // TCP Setup
        IoAcceptor tcpAcceptor = new NioSocketAcceptor();
        tcpAcceptor.setHandler(new DualProtocolHandler("TCP"));
        tcpAcceptor.bind(new InetSocketAddress(PORT));

        // UDP Setup
        IoAcceptor udpAcceptor = new NioDatagramAcceptor();
        ((DatagramSessionConfig) udpAcceptor.getSessionConfig()).setReuseAddress(true);
        udpAcceptor.setHandler(new DualProtocolHandler("UDP"));
        udpAcceptor.bind(new InetSocketAddress(PORT));

        System.out.println("Server listening on port " + PORT + " (TCP & UDP)");
    }
}

class DualProtocolHandler extends IoHandlerAdapter {
    private final String protocol;

    public DualProtocolHandler(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        String received = message.toString();
        System.out.println(protocol + " received: " + received);
        session.write(protocol + " Server received: " + received);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        session.closeNow();
    }
}