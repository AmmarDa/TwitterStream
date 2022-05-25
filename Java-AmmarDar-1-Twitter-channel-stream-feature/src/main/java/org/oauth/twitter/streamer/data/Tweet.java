package org.oauth.twitter.streamer.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Tweet class to represent tweet information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet implements Comparable<Tweet> {

    @JsonProperty(TweetKeywords.MESSAGE_ID)
    private String messageID;

    @JsonProperty(TweetKeywords.TEXT)
    private String text;
    @JsonProperty(TweetKeywords.CREATION_DATE)
    @JsonFormat(pattern = "E MMM dd HH:mm:ss Z yyyy")
    private Date creationDate;
    @JsonProperty(TweetKeywords.AUTHOR)
    private Author author;

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }


    @Override
    public int compareTo(@NotNull Tweet o) {
        return this.getCreationDate().compareTo(o.creationDate);
    }

    @Override
    public String toString(){
        return "Tweet: [" +
                TweetKeywords.MESSAGE_ID + ": " + messageID + "," +
                TweetKeywords.CREATION_DATE + ": " + creationDate + "," +
                TweetKeywords.TEXT + ": " + text + "," +
                TweetKeywords.AUTHOR + ": " + author.toString() +
                "]";
    }

}