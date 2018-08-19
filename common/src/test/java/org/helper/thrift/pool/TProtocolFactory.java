package org.helper.thrift.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class TProtocolFactory extends BasePooledObjectFactory<TProtocol> {


    private String host;
    private int port;
    private boolean keepAlive = true;

    public TProtocolFactory(String host, int port, boolean keepAlive) {
        this.host = host;
        this.port = port;
        this.keepAlive = keepAlive;
    }

    @Override
    public TProtocol create() throws TTransportException {
        TSocket tSocket = new TSocket(host, port);
        TTransport tTransport = new TFramedTransport(tSocket);
        tTransport.open();
        return new TCompactProtocol(tTransport);
    }


    @Override
    public PooledObject<TProtocol> wrap(TProtocol protocol) {
        return new DefaultPooledObject<>(protocol);
    }

    /**
     * 对象钝化(即：从激活状态转入非激活状态，returnObject时触发）
     *
     * @param pooledObject
     * @throws TTransportException
     */
    @Override
    public void passivateObject(PooledObject<TProtocol> pooledObject) throws TTransportException {
        if (!keepAlive) {
            pooledObject.getObject().getTransport().flush();
            pooledObject.getObject().getTransport().close();
        }
    }


    /**
     * 对象激活(borrowObject时触发）
     *
     * @param pooledObject
     * @throws TTransportException
     */
    @Override
    public void activateObject(PooledObject<TProtocol> pooledObject) throws TTransportException {
        if (!pooledObject.getObject().getTransport().isOpen()) {
            pooledObject.getObject().getTransport().open();
        }
    }


    /**
     * 对象销毁(clear时会触发）
     *
     * @param pooledObject
     * @throws TTransportException
     */
    @Override
    public void destroyObject(PooledObject<TProtocol> pooledObject) throws TTransportException {
        passivateObject(pooledObject);
        pooledObject.markAbandoned();
    }


    /**
     * 验证对象有效性
     *
     * @param p
     * @return
     */
    @Override
    public boolean validateObject(PooledObject<TProtocol> p) {
        if (p.getObject() != null) {
            if (p.getObject().getTransport().isOpen()) {
                return true;
            }
            try {
                p.getObject().getTransport().open();
                return true;
            } catch (TTransportException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
