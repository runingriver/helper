package org.helper.common.aio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author hzz 18-1-18
 */
public class AIOClient {
    private static final Logger logger = LoggerFactory.getLogger(AIOClient.class);

    public static void main(String[] args) throws InterruptedException {
        try {
            client();
        } catch (IOException | InterruptedException | ExecutionException e) {
            logger.info("error:{}", e);
        }

        TimeUnit.SECONDS.sleep(100);
    }

    private static void client() throws InterruptedException, IOException, ExecutionException {
        final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        Future<Void> future = client.connect(new InetSocketAddress("127.0.0.1", 8014));
        future.get();

        final ByteBuffer buffer = ByteBuffer.allocate(100);

        client.read(buffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                logger.info("client received: {}", new String(buffer.array()));
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                logger.info("error:{}", exc);
                try {
                    client.close();
                } catch (IOException e) {
                    logger.info("error:{}", exc);
                }
            }
        });



        TimeUnit.SECONDS.sleep(60);
    }
}
