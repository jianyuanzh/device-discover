package xyz.yflog.common.util.ping;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by vincent on 15/6/7.
 */
public interface IPing extends Closeable {

    PingResult ping(InetAddress host, int count) throws IOException;

    PingResult ping(String hostname, int count) throws IOException;
}
