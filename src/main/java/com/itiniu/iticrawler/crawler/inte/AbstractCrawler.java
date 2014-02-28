package com.itiniu.iticrawler.crawler.inte;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import com.itiniu.iticrawler.crawler.PageExtractionType;
import com.itiniu.iticrawler.exceptions.InputStreamPageExtractionException;
import org.apache.commons.io.input.TeeInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.xml.sax.SAXException;

import com.itiniu.iticrawler.behaviors.inte.ICrawlBehavior;
import com.itiniu.iticrawler.behaviors.inte.IRobotTxtBehavior;
import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.impl.DefaultPage;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.httptools.inte.HttpConnectionManagerInterf;
import com.itiniu.iticrawler.livedatastorage.inte.IProcessedURLStore;
import com.itiniu.iticrawler.livedatastorage.inte.IRobotTxtStore;
import com.itiniu.iticrawler.livedatastorage.inte.IScheduledURLStore;

import static com.itiniu.iticrawler.crawler.PageExtractionType.*;


/**
 * Crawler. Objects of this class are used to run inside crawling threads. They
 * do the page processing and the scheduling.
 *
 * @author esquive
 */
public abstract class AbstractCrawler implements Runnable {
    //Getting the logger
    protected static final Logger logger = LogManager.getLogger(AbstractCrawler.class);

    // The Data holders
    private IScheduledURLStore scheduledUrls = null;
    private IProcessedURLStore processedUrls = null;
    private IRobotTxtStore robotTxtData = null;

    // The Behaviors
    private ICrawlBehavior customCrawlBehavior = null;
    private IRobotTxtBehavior robotTxtBehavior = null;

    // The HttpTools
    private HttpClient httpClient = null;
    private HttpConnectionManagerInterf httpConnectionManager = null;

    // Crawler relevant variables
    private boolean busy = false;
    private PageExtractionType extractionType;


    @Override
    public void run() {
        this.execute();
    }

    /**
     *
     */
    private void execute() {
        URLWrapper cUrl = null;
        boolean shouldRun = true;
        int schedulerReturnedNullCounter = 0;

        while (shouldRun) {
            cUrl = this.scheduledUrls.getNextURL();

            if (cUrl != null) {
                this.busy = true;

                if (!this.processedUrls.isCurrentlyProcessedUrl(cUrl)
                        && !this.processedUrls.wasProcessed(cUrl)) {
                    this.processedUrls.addCurrentlyProcessedUrl(cUrl);

                    if (!this.robotTxtData.containsRule(cUrl)) {
                        this.robotTxtBehavior.fetchRobotTxt(cUrl,
                                this.httpConnectionManager.getNewHttpClient(), this.robotTxtData);
                    }
                    if (this.robotTxtData.allows(cUrl)) {
                        // check for the politeness (I don't need to check if
                        // the
                        // page was processed before since in the scheduler
                        // usually I only have
                        // single values)
                        // Naaaaa change of mind:
                        // It might happen that URLs get scheduled twice
                        // Because of locking is more
                        // efficient to check twice if it was already
                        // processed

                        // Getting a timeStamp to determine if I can request
                        // the host again
                        long timeStamp = this.processedUrls.lastHostProcessing(cUrl)
                                + ConfigSingleton.INSTANCE.getPolitnessDelay();

                        if (timeStamp <= System.currentTimeMillis()) {
                            // TODO: catch the exceptions here and not at
                            // the lower method level:
                            // This is relevant for the URL scheduling.

                            try
                            {
                                switch (this.extractionType)
                                {
                                    case BY_STRING:
                                        break;
                                    case BY_INPUTSTREAM:
                                        this.crawlPageInputStreamHandling(cUrl);
                                        break;
                                    case BY_OUTPUTSTREAM:
                                        break;
                                    default:
                                        this.crawlPageInputStreamHandling(cUrl);
                                        break;
                                }
                            }
                            catch(InputStreamPageExtractionException e)
                            {
                                logger.error("Error in the extraction process", e);
                            }

                            // Setting the politeness Timestamp for future
                            // access to the host
                            this.processedUrls.addProcessedHost(cUrl, System.currentTimeMillis());

                            this.processedUrls.addProcessedURL(cUrl);
                            this.processedUrls.removeCurrentlyProcessedUrl(cUrl);

                        } else {
                            this.scheduledUrls.scheduleURL(cUrl);
                            this.processedUrls.removeCurrentlyProcessedUrl(cUrl);
                        }
                    }
                }

                this.busy = false;

            } else {
                schedulerReturnedNullCounter++;

                if (schedulerReturnedNullCounter == 10) {
                    shouldRun = false;
                }

            }

        }// End of the while loop

    }

    /**
     * @param url
     * @return
     */
    @Deprecated
    private void extractData(URLWrapper url) {
        AbstractPage toReturn = null;
        HttpGet request = null;
        CloseableHttpResponse response = null;
        InputStream htmlStream = null;


        int pageStatus = -1;

        try {
            // Making the request
            request = new HttpGet(url.toString());
            response = (CloseableHttpResponse) this.httpClient.execute(request);

            pageStatus = response.getStatusLine().getStatusCode();


            if (pageStatus == HttpStatus.SC_OK) {
                // Getting the content
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    htmlStream = entity.getContent();

                    //Do all the document parsing here links and htmlContent
                    LinkContentHandler links = new LinkContentHandler();
                    ToHTMLContentHandler html = new ToHTMLContentHandler();
                    TeeContentHandler teeHandler = new TeeContentHandler(links, html);

                    Metadata metadata = new Metadata();
                    metadata.add(Metadata.CONTENT_LOCATION, url.toString());
                    metadata.add(Metadata.RESOURCE_NAME_KEY, url.toString());

                    HtmlParser parser = new HtmlParser();
                    parser.parse(htmlStream,
                            teeHandler,
                            metadata,
                            new ParseContext());

                    // //Process the page content
                    toReturn = new DefaultPage();
                    toReturn.setUrl(url);
                    toReturn.setHtml(html.toString());
                    toReturn.setOutgoingURLs(links.getLinks());


                } else {
                    //TODO: throw an exception
                }
            } else if (pageStatus == HttpStatus.SC_NOT_FOUND) {
                logger.info("URL not found: " + url.toString());

            } else if ((pageStatus == HttpStatus.SC_MOVED_TEMPORARILY)
                    || (pageStatus == HttpStatus.SC_MOVED_PERMANENTLY)) {
                Header header = response.getFirstHeader("Location");
                if (header != null) {
                    // TODO: Implement the redirect
                }
            }

        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
            // TODO: remove the processed URL from the processedUrls data holder
            // NOT SURE THEY SHOULD STAY THERE
        } catch (IOException e2) {
            e2.printStackTrace();
            // TODO: remove the processed URL from the processedUrls data holder
            // NOT SURE THEY SHOULD STAY THERE
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TikaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            try {
                if (htmlStream != null) htmlStream.close();
                if (response != null) response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }


    public void crawlPageInputStreamHandling(URLWrapper url) throws InputStreamPageExtractionException {

        boolean toReturn = true;
        AbstractPage page = null;
        HttpGet request = null;
        CloseableHttpResponse response = null;
        InputStream htmlStream = null;
        InputStream pageStream = null;

        int pageStatus = -1;

        try {
            //Initializing a page object
            page = new DefaultPage();
            page.setUrl(url);

            // Making the request
            request = new HttpGet(url.toString());
            response = (CloseableHttpResponse) this.httpClient.execute(request);

            //Handling the returned statuscode
            pageStatus = response.getStatusLine().getStatusCode();
            page.setStatusCode(pageStatus);
            this.customCrawlBehavior.handleStatuScode(page);

            if (page.isContinueProcessing()) {
                if (pageStatus == HttpStatus.SC_OK) {

                    // Getting the content
                    HttpEntity entity = response.getEntity();

                    if (entity != null) {

                        pageStream = new PipedInputStream();
                        final InputStream tHtmlStream = new BufferedInputStream(new TeeInputStream(entity.getContent(), new PipedOutputStream((PipedInputStream) pageStream), true));

                        final LinkContentHandler tHandler = new LinkContentHandler();

                        //Do all the document parsing here links and htmlContent
                        final Metadata metadata = new Metadata();
                        metadata.add(Metadata.CONTENT_LOCATION, url.toString());
                        metadata.add(Metadata.RESOURCE_NAME_KEY, url.toString());

                        final HtmlParser parser = new HtmlParser();

                        //Parsing the html for the urls in a separate thread
                        Thread t = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    parser.parse(tHtmlStream,
                                            tHandler,
                                            metadata,
                                            new ParseContext());


                                } catch (IOException e) {
                                    logger.error("IOException in parsing thread", e);
                                } catch (SAXException e) {
                                    logger.error("SAXException in parsing thread", e);
                                } catch (TikaException e) {
                                    logger.error("TikaException in parsing thread", e);
                                }
                                finally {
                                    try {
                                        tHtmlStream.close();
                                    } catch (IOException e) {
                                        logger.error("IOException in while closing the stream in parsing thread", e);
                                    }
                                }
                            }
                        });
                        t.start();

                        //Set the stream of the page and process it by user code
                        page.setStream(pageStream);
                        this.processPage(page);

                        //Waiting for the processing thread to finish
                        t.join();
                        htmlStream = tHtmlStream;

                        if(page.isContinueProcessing()){
                            //Set the urls extracted from the page, and schedule them by user code.
                            page.setOutgoingURLs(tHandler.getLinks());
                            this.scheduleURLs(page);
                        }
                    } else {
                        throw new InputStreamPageExtractionException("Error in the Http request process");
                    }
                } else if (pageStatus == HttpStatus.SC_NOT_FOUND) {
                    logger.info("URL not found: " + url.toString());

                } else if ((pageStatus == HttpStatus.SC_MOVED_TEMPORARILY)
                        || (pageStatus == HttpStatus.SC_MOVED_PERMANENTLY)) {
                    Header header = response.getFirstHeader("Location");
                    if (header != null) {
                        // TODO: Implement the redirect
                    }
                }
            }

        } catch (ClientProtocolException e1) {
            logger.error("ClientProtocolException during the crawl process", e1);
        } catch (IOException e2) {
            logger.error("IOException during the crawl process",e2);
        } catch (InterruptedException e) {
            logger.error("InterruptedException during the crawl process",e);
        } finally {
            try {
                if(htmlStream != null) htmlStream.close();
                if (response != null) response.close();
            } catch (IOException e) {
                logger.error("IOException while closing the htmlStream or the server Response in parsing thread", e);
            }
        }
    }


    /**
     * @param page
     */
    private void processPage(AbstractPage page) {
        this.customCrawlBehavior.processPage(page);
    }

    /**
     * @param page
     */
    private void scheduleURLs(AbstractPage page) {
        for (URLWrapper cUrl : page.getOutgoingURLs()) {
            if (!this.processedUrls.wasProcessed(cUrl)
                    && !this.processedUrls.isCurrentlyProcessedUrl(cUrl)) {

                if ((page.getUrl().getUrlDepth() + 1) != ConfigSingleton.INSTANCE
                        .getMaxCrawlDepth()) {
                    cUrl.setUrlDepth(page.getUrl().getUrlDepth() + 1);
                    cUrl.setParentURL(page.getUrl());

                    if (this.customCrawlBehavior.shouldScheduleURL(page, cUrl)) {
                        this.scheduledUrls.scheduleURL(cUrl);
                    }
                }
            }
        }
    }




    // -----------Getters and Setters--------------------//

    public void setCustomCrawlBehavior(ICrawlBehavior customCrawlBehavior) {
        this.customCrawlBehavior = customCrawlBehavior;
    }

    public void setHttpConnectionManager(HttpConnectionManagerInterf httpConnectionManager) {
        this.httpConnectionManager = httpConnectionManager;
    }

    public void setRobotTxtBehavior(IRobotTxtBehavior robotTxtBehavior) {
        this.robotTxtBehavior = robotTxtBehavior;

    }

    public void setScheduledUrlsData(IScheduledURLStore scheduledUrls) {
        this.scheduledUrls = scheduledUrls;
    }

    public void setProcessedUrlsData(IProcessedURLStore processedUrls) {
        this.processedUrls = processedUrls;
    }

    public void setRobotTxtData(IRobotTxtStore robotTxtData) {
        this.robotTxtData = robotTxtData;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public boolean isBusy() {
        return this.busy;
    }

    public void setExtractionType(PageExtractionType extractionType)
    {
        this.extractionType = extractionType;
    }

    public PageExtractionType getExtractionType()
    {
        return this.extractionType;
    }


}
