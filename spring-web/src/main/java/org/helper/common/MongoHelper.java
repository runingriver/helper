package org.helper.common;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Mongo已实现了连接池，且线程安全
 */
public class MongoHelper {
    private static final Logger logger = LoggerFactory.getLogger(MongoHelper.class);
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;

    private MongoCollection<Document> mongoCollection;

    static {
        String address = null;
        int port = 0;
        String dbName = null;
        String user = null;
        char[] password = null;
        try {
            address = FileProperty.getPropertyValues("MONGO_ADD");
            port = NumberUtils.toInt(FileProperty.getPropertyValues("MONGO_PORT"));
            dbName = FileProperty.getPropertyValues("MONGO_DBNAME");
            user = FileProperty.getPropertyValues("MONGODB_USER");
            password = FileProperty.getPropertyValues("MONGODB_PASSWORD").toCharArray();
            List<ServerAddress> seeds = Lists.newArrayList(new ServerAddress(address, port));
            MongoCredential credentials = MongoCredential.createScramSha1Credential(user, dbName, password);
            List<MongoCredential> credentialsList = Lists.newArrayList(credentials);
            mongoClient = new MongoClient(seeds, credentialsList);
            mongoDatabase = mongoClient.getDatabase(dbName);
        } catch (Exception e) {
            logger.error("mongo initial exception", e);
        } finally {
            logger.info("mongo address:{},port:{},dbName:{},user:{},pwd:{}", address, port, dbName, user, password);
        }
    }

    public static MongoHelper getMongoCollection(String collectionName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(collectionName), "collectionName error.");
        MongoHelper mongoHelper = new MongoHelper();
        mongoHelper.mongoCollection = mongoDatabase.getCollection(collectionName);
        return mongoHelper;
    }

    /**
     * 批量插入,线程安全
     *
     * @param documentList
     */
    public boolean batchInsert(List<Document> documentList) {
        if (documentList == null || documentList.isEmpty()) {
            logger.error("documentList is empty!");
            return false;
        }
        logger.info("---batch inset mongo num:{}---", documentList.size());
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            mongoCollection.insertMany(documentList);
        } catch (Exception e) {
            logger.error("insert document exception.documentList:{}", documentList, e);
            return false;
        }

        logger.info("---batch insert mongo success.insert num:{},cost:{}---", documentList.size(),
                stopwatch.stop().toString());
        return true;
    }


}
