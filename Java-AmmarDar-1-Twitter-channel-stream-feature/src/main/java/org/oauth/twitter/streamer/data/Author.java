package org.oauth.twitter.streamer.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Tweet user class.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Author {

    @JsonProperty(TweetKeywords.AUTHOR_ID)
    private String authorID;
    @JsonProperty(TweetKeywords.AUTHOR_NAME)
    private String name;

    @JsonProperty(TweetKeywords.AUTHOR_SCREEN_NAME)
    private String screenName;
    @JsonProperty(TweetKeywords.CREATION_DATE)
    @JsonFormat(pattern = "E MMM dd HH:mm:ss Z yyyy")
    private Date creationDate;

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }


    @Override
    public String toString(){
        return "Author: [" +
                TweetKeywords.AUTHOR_ID + ": " + getAuthorID() + "," +
                TweetKeywords.CREATION_DATE + ": " + creationDate + "," +
                TweetKeywords.AUTHOR_NAME + ": " + name + "," +
                TweetKeywords.AUTHOR_SCREEN_NAME + ": " + screenName +
                "]";
    }

}
