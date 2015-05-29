package com.itiniu.iticrawler.config;



import com.itiniu.iticrawler.util.enums.EvictionPolicy;
import com.itiniu.iticrawler.behaviors.crawler.CrawlBehavior;
import com.itiniu.iticrawler.util.enums.PageExtractionType;

public enum ConfigSingleton {

    INSTANCE;

    //Config Relevant
    private boolean fileConfigLoaded = false;
    private boolean clusterConfigLoaded = false;



    // Http Connection relevant
    private int maxConnections = 100;
    private int maxConnectionsPerHost = 100;
    private int socketTimeout = 20000;
    private int connectionTimeout = 30000;

    public void setMaxConnections(int param) {
        maxConnections = param;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnectionsPerHost(int param) {
        maxConnectionsPerHost = param;
    }

    public int getMaxConnectionsPerHost() {
        return maxConnectionsPerHost;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }


    // ---------------------------------------------------------------------------------------------------------------

    // Real-time Data-Storage relevant

    private ClusterConfig clusterConfig;
    private EvictionPolicy eviction = EvictionPolicy.LRU;
    private int maxInMemoryElements = 100;
    private String fileStorage = "storage";

    public void setClusterConfig(ClusterConfig clusterConfig) {
        this.clusterConfig = clusterConfig;
    }

    public ClusterConfig getClusterConfig() {
        return this.clusterConfig;
    }

    public int getMaxInMemoryElements() {
        return maxInMemoryElements;
    }

    public void setMaxInMemoryElements(int maxInMemoryElements) {
        this.maxInMemoryElements = maxInMemoryElements;
    }

    public EvictionPolicy getEviction() {
        return eviction;
    }

    public void setEviction(EvictionPolicy eviction) {
        this.eviction = eviction;
    }

    public String getFileStorage() {
        return fileStorage;
    }

    public void setFileStorage(String fileStorage) {
        this.fileStorage = fileStorage;
    }

    // ---------------------------------------------------------------------------------------------------------------------

    // Crawling relevant
    private int numberOfCrawlerThreads = 10;
    private boolean considerRobotTxt = true;
    private int politnessDelay = 1000;
    private int maxCrawlDepth = -1;
    private String userAgent = "itiCrawler";
    private boolean stopOnInactivity = false;
    private PageExtractionType extractionType = PageExtractionType.BY_STREAM;
    private boolean followRedirect = true;
    private int maxHostsToCrawl = 0;

    private Class<? extends CrawlBehavior> customCrawlBehavior = null;

    public int getNumberOfCrawlerThreads() {
        return numberOfCrawlerThreads;
    }

    public void setNumberOfCrawlerThreads(int numberOfCrawlerThreads) {
        this.numberOfCrawlerThreads = numberOfCrawlerThreads;
    }

    public boolean isConsiderRobotTxt() {
        return considerRobotTxt;
    }

    public void setConsiderRobotTxt(boolean considerRobotTxt) {
        this.considerRobotTxt = considerRobotTxt;
    }

    public int getPolitnessDelay() {
        return politnessDelay;
    }

    public void setPolitnessDelay(int politnessDelay) {
        this.politnessDelay = politnessDelay;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getMaxCrawlDepth() {
        return maxCrawlDepth;
    }

    public void setMaxCrawlDepth(int maxCrawDepth) {
        this.maxCrawlDepth = maxCrawDepth;
    }

    public Class<? extends CrawlBehavior> getCustomCrawlBehavior() {
        Class<? extends CrawlBehavior> toReturn = customCrawlBehavior;

        return toReturn;
    }

    public void setCustomCrawlBehavior(Class<? extends CrawlBehavior> customCrawlBehavior) {
        this.customCrawlBehavior = customCrawlBehavior;
    }

    public boolean isStopOnInactivity() {
        return stopOnInactivity;
    }

    public void setStopOnInactivity(boolean stopOnInactivity) {
        this.stopOnInactivity = stopOnInactivity;
    }

    public PageExtractionType getExtractionType() {
        return extractionType;
    }

    public void setExtractionType(PageExtractionType extractionType) {
        this.extractionType = extractionType;
    }

    public boolean isFollowRedirect() {
        return followRedirect;
    }

    public void setFollowRedirect(boolean followRedirect) {
        this.followRedirect = followRedirect;
    }

    public int getMaxHostsToCrawl() {
        return maxHostsToCrawl;
    }

    public void setMaxHostsToCrawl(int maxHostsToCrawl) {
        this.maxHostsToCrawl = maxHostsToCrawl;
    }

}
