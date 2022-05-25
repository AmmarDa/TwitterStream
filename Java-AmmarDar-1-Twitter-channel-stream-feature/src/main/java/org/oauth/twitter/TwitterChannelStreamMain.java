package org.oauth.twitter;

import com.google.api.client.http.HttpRequestFactory;
import org.oauth.twitter.streamer.TwitterChannelStreamImpl;
import org.oauth.twitter.TwitterAuthenticationException;
import org.oauth.twitter.TwitterAuthenticator;

import java.io.IOException;

public class TwitterChannelStreamMain {

    private static TwitterAuthenticator twitterAuthenticator;
    private static final String PROPERTIES_FILE = "resource/stream.properties";

    public static void main(String[] args) {
        twitterAuthenticator = new TwitterAuthenticator(System.out, "RLSrphihyR4G2UxvA0XBkLAdl",
                "FTz2KcP1y3pcLw0XXMX5Jy3GTobqUweITIFy4QefullmpPnKm4");
        try {
            HttpRequestFactory httpRequestFactory = twitterAuthenticator.getAuthorizedHttpRequestFactory();
            TwitterChannelStreamImpl twitterStreamImpl = new TwitterChannelStreamImpl(PROPERTIES_FILE);
            twitterStreamImpl.retrieveTweets(httpRequestFactory);
            twitterStreamImpl.processTweets();
        } catch (TwitterAuthenticationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}