package org.helper.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {

    private static int BUFFER_SIZE = 1024 * 10 * 4;

    private static final Logger logger = LoggerFactory.getLogger(SerializeUtil.class);

    /**
     * 序列化
     * 
     * @param obj
     * @return
     * @throws NullPointerException
     * @throws IOException
     */
    public static byte[] serialize(Object obj) throws NullPointerException {
        if (obj == null) {
            throw new NullPointerException("SerializeUtil.serialize() argc obj == null");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
        ObjectOutputStream objStream = null;
        byte[] bytes = null;
        try {
            objStream = new ObjectOutputStream(out);
            objStream.writeObject(obj);
            bytes = out.toByteArray();

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (objStream != null) {
                try {
                    objStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return bytes;
    }

    /**
     * 反序列化
     * 
     * @param byteArray
     * @return
     * @throws NullPointerException
     * @throws IOException
     */
    public static Object deserialize(byte[] byteArray) throws NullPointerException {
        if (byteArray == null || byteArray.length == 0) {
            throw new NullPointerException(
                    "SerializeUtil.deserialize() byteArray == null or length is 0");
        }

        ByteArrayInputStream in = new ByteArrayInputStream(byteArray);
        ObjectInputStream objStream = null;
        Object obj = null;
        try {
            objStream = new ObjectInputStream(in);
            obj = objStream.readObject();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (objStream != null) {
                    objStream.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return obj;
    }
}