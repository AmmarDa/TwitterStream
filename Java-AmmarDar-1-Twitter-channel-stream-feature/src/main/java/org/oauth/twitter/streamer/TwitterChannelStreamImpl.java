package org.oauth.twitter.streamer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import org.oauth.twitter.streamer.data.Author;
import org.oauth.twitter.streamer.data.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Twitter channel stream implementation that will collect tweets then log them into a log files.
 */
public class TwitterChannelStreamImpl extends TwitterChannelStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterChannelStreamImpl.class);

    private static final GenericUrl END_POINT =
            new GenericUrl("https://stream.twitter.com/1.1/statuses/filter.json?track=bieber");

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    protected List<Tweet> tweets;

    protected Map<Author, List<Tweet>> tweetsByAuthor;

    private static final String TIMEOUT_MILLI_SECONDS = "TIMEOUT_MILLI_SECONDS";
    private static final String MESSAGES_THRESHOLD = "MESSAGES_THRESHOLD";

    private int timeout;
    private int messageThreshold;

    public TwitterChannelStreamImpl(@NotNull final String propertiesFileName) throws IOException {
        Properties properties = loadPropertiesFile(propertiesFileName);
        timeout = Integer.parseInt(properties.getProperty(TIMEOUT_MILLI_SECONDS));
        messageThreshold = Integer.parseInt(properties.getProperty(MESSAGES_THRESHOLD));

    }

    /***
     /**
     * Load the properties file that contains parameters of the stream channel
     * @param propertiesFileName The properties file relative path
     * @return Return the properties file
     * @throws IOException Throw IOException if the Property file isn't loaded
     */
    private Properties loadPropertiesFile(final String propertiesFileName) throws IOException {
        InputStream iStream = null;
        try {
            iStream = Files.newInputStream(Paths.get(propertiesFileName));
            Properties properties = new Properties();
            properties.load(iStream);
            return properties;
        } finally {
            try {
                if(iStream != null){
                    iStream.close();
                }
            } catch (IOException e) {
                throw new IOException("Properties file: " + propertiesFileName + "can't be closed", e);
            }
        }
    }

    /**
     * Collect tweets from the specified Twitter end point.
     * @param httpRequestFactory Request to the end point.
     * @return List of tweets that match the specified conditions
     * @throws IOException Throw IOException if the end point status is not OK.
     */
    @Override
    public void retrieveTweets(HttpRequestFactory httpRequestFactory) throws IOException {
        HttpRequest request = httpRequestFactory.buildGetRequest(END_POINT);
        HttpResponse response = request.execute();
        if (response.getStatusCode() != 200) {
            throw new IOException("Unexpected return code: "
                    + response.getStatusCode()
                    + "\nMessage:\n"
                    + response.getStatusMessage());
        }
        InputStream inputStream =  response.getContent();
        tweets = readTweetsFromInputStream(inputStream, timeout);
    }

    /**
     * Read tweets from the specified inputSteam and encapsulate them into Tweet objects
     * @param is InputStream to the endpoint
     * @param timeoutMillis Timeout
     * @return List of Tweets
     * @throws IOException Throw IOException if we can't read from the endpoint throw the buffer reader.
     */
    private List<Tweet> readTweetsFromInputStream(@NotNull InputStream is, int timeoutMillis)
            throws IOException  {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        List<Tweet> tweets = new ArrayList<>();
        long maxMilliSeconds = System.currentTimeMillis() + timeoutMillis;
        int msgsCounter = 0;
        while(bufferedReader.ready() && msgsCounter < messageThreshold &&
                System.currentTimeMillis() < maxMilliSeconds) {
            String message = bufferedReader.readLine();
            tweets.add(OBJECT_MAPPER.readValue(message, Tweet.class));
            msgsCounter++;
        }
        return tweets;
    }

    protected void sortTweetsByUsersCreationDate() {
        tweetsByAuthor = tweets.stream()
                .sorted(Comparator.comparing(p -> p.getAuthor().getCreationDate()))
                .collect(Collectors.groupingBy(Tweet::getAuthor, LinkedHashMap::new, Collectors.toList()));

        for (List<Tweet> userTweetList : tweetsByAuthor.values()) {
            userTweetList.sort((p1, p2) -> p1.compareTo(p2));
        }
    }


    /**
     * Process the tweets by ordering it chronologically based on Author creation date, then group them by author ID
     * and print them to the output log system in the following style
     * Author [author attributes]
     * Tweet1 [tweet attributes]
     * Tweet2 [tweet attributes]
     * -------------------
     */
    @Override
    public void processTweets() throws IOException {
        sortTweetsByUsersCreationDate();
        Iterator<Author> users =  tweetsByAuthor.keySet().iterator();
        for (List<Tweet> userTweetList : tweetsByAuthor.values()) {
            if (users.hasNext())
                LOGGER.info(users.next().toString() + "\n");
            for(Tweet tweet: userTweetList) {
                LOGGER.info(tweet.toString()+ "\n");
            }
            LOGGER.info("-----------------"+ "\n");
        }

    }
}