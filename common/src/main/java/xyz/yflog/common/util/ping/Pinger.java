package xyz.yflog.common.util.ping;

import java.io.Closeable;
import java.net.InetAddress;

/**
 * Created by vincent on 15/6/7.
 */
public interface Pinger extends Closeable {

    PingResult ping(InetAddress host, int count);

    PingResult ping(String hostname, int count);
}
