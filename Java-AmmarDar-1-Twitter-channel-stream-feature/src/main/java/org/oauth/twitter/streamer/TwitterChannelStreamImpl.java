package org.oauth.twitter.streamer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import org.oauth.twitter.streamer.data.Author;
import org.oauth.twitter.streamer.data.Tweet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;


/**
 * Twitter channel stream implementation that will collect tweets then log them into a log files.
 */
public class TwitterChannelStreamImpl extends TwitterChannelStream {

    private List<Tweet> tweets;
    private Map<String, List<Tweet>> tweetByUser;
    private static final GenericUrl END_POINT =
            new GenericUrl("https://stream.twitter.com/1.1/statuses/filter.json?track=bieber");

    private static final String TIMEOUT_MILLI_SECONDS = "TIMEOUT_MILLI_SECONDS";
    private static final String MESSAGES_THRESHOLD = "MESSAGES_THRESHOLD";

    final private int timeout;
    final private int messageThreshold;

    /***
     * Initialized the Twitter channel stream with the max number of messages and timeout
     * @param messageThreshold max number of messages
     * @param timeout The allowed timeout for the channel to be opened
     */
    public TwitterChannelStreamImpl(int messageThreshold, int timeout) {
        this.messageThreshold = messageThreshold;
        this.timeout = timeout;
    }

    /**
     * Collect tweets from the specified Twitter end point.
     * @param httpRequestFactory Request to the end point.
     * @return List of tweets
     * @throws IOException
     */
    @Override
    public void collectTweets(final HttpRequestFactory httpRequestFactory) throws IOException {
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
     * @return List of tweets gathered from endpoint
     * @throws IOException
     */
    private List<Tweet> readTweetsFromInputStream(@NotNull InputStream is, int timeoutMillis)
            throws IOException  {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        ObjectMapper objectMapper = new ObjectMapper();
        List<Tweet> tweets = new ArrayList<>();
        long maxMilliSeconds = System.currentTimeMillis() + timeoutMillis;
        int msgsCounter = 0;
        while(bufferedReader.ready() && msgsCounter < messageThreshold &&
                System.currentTimeMillis() < maxMilliSeconds) {
            String message = bufferedReader.readLine();
            tweets.add(objectMapper.readValue(message, Tweet.class));
            msgsCounter++;
        }
        return tweets;
    }


    /**
     * Process Tweets group them by User and sort them Chronologically based on Message creation date
     */
    @Override
    public void processTweets() {
        tweetByUser = tweets.stream()
                .sorted((p1,p2) -> p1.getUser().getCreationDate().compareTo(p2.getUser().getCreationDate()))
                .collect(Collectors.groupingBy(
                        p -> p.getUser().getAuthorID()));


        for (List<Tweet> userTweetList : tweetByUser.values()) {
            userTweetList.sort((p1, p2) -> p1.compareTo(p2));
        }

        printSortedTweets();
    }

    private void printSortedTweets() {

        for (List<Tweet> userTweetList : tweetByUser.values()) {
            for(Tweet tweet: userTweetList) {
                System.out.println(tweet.toString());
            }
        }
    }
}
