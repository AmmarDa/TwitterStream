package org.oauth.twitter.streamer;

import com.google.api.client.http.HttpRequestFactory;
import java.io.IOException;

/**
 * Twitter channel stream base class. The aim of this class is to call the Twitter end point to collect the
 * tweets and then process them.
 */
public abstract class TwitterChannelStream {

    public abstract void retrieveTweets(HttpRequestFactory httpRequestFactory) throws IOException;

    public abstract void processTweets();
}