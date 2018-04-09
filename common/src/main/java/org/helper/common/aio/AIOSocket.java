package org.helper.common.aio;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author hzz 18-1-18
 */
public class AIOSocket {
    private static final Logger logger = LoggerFactory.getLogger(AIOSocket.class);
    private static Charset charset = Charset.forName("UTF-8");
    private static CharsetEncoder encoder = charset.newEncoder();

    public static void main(String[] args) throws InterruptedException {
        try {
            sever();
        } catch (IOException | InterruptedException e) {
            logger.info("error:{}", e);
        }
    }

    private static void sever() throws IOException, InterruptedException {
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(4));

        final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group)
                .bind(new InetSocketAddress("0.0.0.0", 8014));

        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Void attachment) {
                server.accept(null, this); // 接受下一个连接
                try {
                    String now = DateTime.now().toString("yyyy-MM-dd hh:mm:ss.SSS");
                    ByteBuffer buffer = encoder.encode(CharBuffer.wrap(now + "\r\n"));
                    //采用callback方式result.write(buffer, null, new CompletionHandler<Integer,Void>(){...});
                    Future<Integer> f = result.write(buffer);
                    f.get();
                    logger.info("sent to client: {}", now);
                    result.close();
                } catch (IOException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                logger.info("error:{}", exc);
            }
        });

        group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

    }


}
