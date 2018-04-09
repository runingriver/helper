package org.helper.common;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HbaseHelper {
    private static final Logger logger = LoggerFactory.getLogger(HbaseHelper.class);
    private Configuration conf = HBaseConfiguration.create();
    private HConnection connection;
    //这里单表只开启一个connection读写,可以升级为多线程对应多个connection读写.
    private Map<String, HTableInterface> tableMap = new ConcurrentHashMap<>();

    public static final String ADDRESS = "";
    public static final String PATH = "";
    public static final String PORT = "";
    public static final String CF = "";


    @PostConstruct
    public void init() {
        //zookeeper地址
        conf.set("hbase.zookeeper.quorum", ADDRESS);
        //zookeeper端口
        conf.set("hbase.zookeeper.property.clientPort", PORT);
        //hbase集群在zookeeper中的namespaces
        conf.set("zookeeper.znode.parent", PATH);
        try {
            connection = HConnectionManager.createConnection(conf);
        } catch (IOException e) {
            logger.error("init hbase connection exception.", e);
        }
    }

    @PreDestroy
    public void destroy() {
        for (HTableInterface table : tableMap.values()) {
            try {
                table.close();
            } catch (IOException e) {
                logger.error("close table:{} exception.", table.getName(), e);
            }
        }

        try {
            connection.close();
        } catch (IOException e) {
            logger.error("close the HConnection exception.", e);
        }
    }

    /**
     * 根据表名获取Hbase表对象
     */
    public HTableInterface getTable(String tableName) throws IOException {
        Preconditions.checkArgument(StringUtils.isNotBlank(tableName), "parameter tableName illegal.");
        if (tableMap.containsKey(tableName)) {
            return tableMap.get(tableName);
        }
        HTableInterface table = connection.getTable(tableName);
        tableMap.put(tableName, table);

        return table;
    }

    /**
     * 插入和更新Hbase表一个cell,value不能为null
     * 插入更新都是一样的!
     */
    public void putRow(String tableName, String rowKey, String columnFamily, String col, String value) throws IOException {
        Preconditions.checkArgument(StringUtils.isNotBlank(col), "parameter of col is empty.");
        //如果value=null,则赋值为"",避免exception
        if (null == value) {
            value = "";
        }
        HTableInterface table = getTable(tableName);
        Put rowPut = new Put(Bytes.toBytes(rowKey));
        rowPut.add(columnFamily.getBytes(), col.getBytes(), value.getBytes());
        table.put(rowPut);
    }

    /**
     * 删除表一行
     */
    public void deleteRow(String tableName, String rowKey) throws IOException {
        HTableInterface table = getTable(tableName);
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        table.delete(delete);
    }

    /**
     * 根据范围批量删除
     */
    public void deleteByBatch(String tableName, String startRow, String endRow) throws IOException {
        HTableInterface table = getTable(tableName);
        Scan scan = new Scan(Bytes.toBytes(startRow), Bytes.toBytes(endRow));
        //尽量减少IO时间
        scan.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("id"));
        ResultScanner rs = table.getScanner(scan);
        for (Result result : rs) {
            byte[] row = result.getRow();
            Delete delete = new Delete(row);
            table.delete(delete);
        }
    }

    /**
     * 范围scan,取全部cf,全部column数据
     */
    public List<Map<String, String>> scanRange(String tableName, String family, String startrow, String endrow) throws IOException {
        HTableInterface table = getTable(tableName);
        Scan scan = new Scan(Bytes.toBytes(startrow), Bytes.toBytes(endrow));
        ResultScanner rs = table.getScanner(scan);
        List<Map<String, String>> listMap = Lists.newArrayList();

        for (Result result : rs) {
            listMap.add(parseHBaseResult(result, family));
        }

        return listMap;
    }

    /**
     * 范围查询,取特定列数据,如果该列数据不存在,则不返回该列,且不报错!
     *
     * @param startrow 开始范围
     * @param endrow   结束范围
     * @param columns  列集合
     * @return 数据集
     * @throws IOException exception
     */
    public List<Map<String, String>> scanRange(String tableName, String startrow, String endrow, List<String> columns) throws IOException {
        HTableInterface table = getTable(tableName);
        Scan scan = new Scan(Bytes.toBytes(startrow), Bytes.toBytes(endrow));
        for (String column : columns) {
            scan.addColumn(Bytes.toBytes(CF), Bytes.toBytes(column));
        }

        ResultScanner rs = table.getScanner(scan);
        List<Map<String, String>> listMap = Lists.newArrayList();

        for (Result result : rs) {
            listMap.add(parseHBaseResult(result, CF));
        }

        return listMap;
    }

    /**
     * 将Hbase返回的Result对象解析成map
     */
    private Map<String, String> parseHBaseResult(Result result, String family) {
        Map<String, String> resultMap = Maps.newHashMap();
        byte[] row = result.getRow();
        if (null == row) {
            logger.warn("getRow is null,there is none row exist.");
            return resultMap;
        }
        resultMap.put("row", new String(row));

        NavigableMap<byte[], byte[]> familyMap = result.getFamilyMap(family.getBytes());
        for (Map.Entry<byte[], byte[]> entry : familyMap.entrySet()) {
            String key = new String(entry.getKey());
            String value = new String(entry.getValue());
            resultMap.put(key, value);
        }
        return resultMap;
    }

    /**
     * 精确获取一行
     */
    public Map<String, String> getRow(String tableName, String rowKey, String family) throws IOException {
        HTableInterface table = getTable(tableName);
        Get get = new Get(Bytes.toBytes(rowKey));
        Result result = table.get(get);

        return parseHBaseResult(result, family);
    }

    public int countRow(String tableName, String startRow, String endRow) throws IOException {
        HTableInterface table = getTable(tableName);
        Scan scan = new Scan(Bytes.toBytes(startRow), Bytes.toBytes(endRow));
        //尽量减少IO时间
        scan.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("id"));
        ResultScanner rs = table.getScanner(scan);
        int count = 0;
        for (Result result : rs) {
            count++;
        }
        return count;
    }
}
