package com.itiniu.iticrawler.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.itiniu.iticrawler.config.ConfigSingleton;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.IntegerSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import org.apache.cassandra.service.CassandraDaemon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.*;

/**
 * Created by ericfalk on 23/05/15.
 */
public class StorageCluster {

    public static final int DEFAULT_PORT = 9160;
    public static final int DEFAULT_STORAGE_PORT = 7000;

    private static final String CRAWLED_COLUMN_NAME = "CRAWLED_COLUMN";

    private static final Logger LOG = LogManager.getLogger(StorageCluster.class);
    private CassandraDaemon cassandra;
    private Keyspace keyspace;
    private ColumnFamily<Integer, String> crawledUrlColumn;

    private File dataDir;

    //TODO: Remove all the google.io stuff
    private final ExecutorService service = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("EmbeddedCassandra-%d")
                    .build());

    public StorageCluster(String clusterName) {

        //TODO: Refactoring
        this.dataDir = new File ("storage");
        LOG.info("Starting cassandra in dir " + dataDir);
        dataDir.mkdirs();


        try {
            URL templateUrl = StorageCluster.class.getClassLoader().getResource("cassandra-template.yaml");
            if(templateUrl == null)
            {
                //TODO: Throw an Exception here
            }

            //Read the file in String
            String configTemplate = new String(Files.readAllBytes(Paths.get(templateUrl.toURI())));
            String configString = configTemplate.replace("$DIR$",dataDir.getPath())
                    .replace("$PORT$", Integer.toString(DEFAULT_PORT))
                    .replace("$STORAGE_PORT$", Integer.toString(DEFAULT_STORAGE_PORT))
            .replace("$CLUSTERNAME$", clusterName);

            File configFile = new File(dataDir, "cassandra.yaml");
            Files.write(Paths.get(configFile.toURI()),configString.getBytes());

            LOG.info("Cassandra config file: " + configFile.getPath());
            System.setProperty("cassandra.config", "file:" + configFile.getPath());

            try {
                cassandra = new CassandraDaemon();
                cassandra.init(null);
            } catch (IOException e) {
                LOG.error("Error initializing embedded cassandra", e);
                throw e;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
        }
        LOG.info("Started cassandra deamon");

        this.start();
        this.keyspace = this.setupAstyanaxContext(clusterName);
        setupKeyspace();
        setupColumnFamilies();

    }

    public void start() {
        Future<Object> future = service.submit(new Callable<Object>() {
                                                   @Override
                                                   public Object call() throws Exception {
                                                       try {
                                                           cassandra.start();
                                                       } catch (Exception e) {
                                                           e.printStackTrace();
                                                       }
                                                       return null;
                                                   }
                                               }
        );

        try {
            future.get();
        } catch (InterruptedException e) {
            // do nothing
        } catch (ExecutionException e) {
            LOG.error("Error starting embedded cassandra", e);
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        service.shutdownNow();
        cassandra.deactivate();
    }

    private Keyspace setupAstyanaxContext(String clusterName)
    {
        AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
                .forCluster(clusterName)
                .forKeyspace("CrawlerKS")
                .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
                                .setDiscoveryType(NodeDiscoveryType.NONE)
                                .setConnectionPoolType(ConnectionPoolType.TOKEN_AWARE)
                )
                .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("CassandraPool")
                                .setPort(9160)
                                .setMaxConnsPerHost(3)
                                .setSeeds("127.0.0.1:9160")
                )
                .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                .buildKeyspace(ThriftFamilyFactory.getInstance());

        //TODO; build your own keyspace

        context.start();
        return context.getClient();
    }

    private void setupKeyspace()
    {
        try {
            //if(keyspace.describeKeyspace() == null) {
                keyspace.createKeyspace(ImmutableMap.<String, Object>builder()
                                .put("strategy_options", ImmutableMap.<String, Object>builder()
                                        .put("replication_factor", "1")
                                        .build())
                                .put("strategy_class", "SimpleStrategy")
                                .build()
                );
            //}
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    private void setupColumnFamilies()
    {
        //TODO Finish the column Family implementation
        //ColumnFamily<String, String> scheduled = ColumnFamily
        //        .newColumnFamily("scheduled", StringSerializer.get(),
        //                StringSerializer.get());
         this.crawledUrlColumn = ColumnFamily
                .newColumnFamily(CRAWLED_COLUMN_NAME, IntegerSerializer.get(),
                        StringSerializer.get());
        //ColumnFamily<String, String> robotTxt = ColumnFamily
        //        .newColumnFamily("robotTxt", StringSerializer.get(),
        //                StringSerializer.get());

        try {
            keyspace.createColumnFamily(crawledUrlColumn, null);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

    }

    public Keyspace getKeyspace()
    {
        return this.keyspace;
    }

    public ColumnFamily<Integer, String> getCrawledUrlColumn() {
        return crawledUrlColumn;
    }
}
