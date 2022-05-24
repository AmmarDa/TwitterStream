package org.oauth.twitter.streamer;

import com.google.api.client.http.HttpRequestFactory;

import java.io.IOException;

/**
 * Twitter channel stream base class. The aim of this class is to call the twitter end point then collect the
 * tweets and then process them.
 */
public abstract class TwitterChannelStream {

    public abstract void collectTweets(HttpRequestFactory httpRequestFactory) throws IOException;

    public abstract void processTweets();
}
