package com.aayushtuladhar.storm.spout;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

public class TwitterSampleSpout extends BaseRichSpout{

    private TwitterStream twitterStream;

    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;
    private String[] keywords;

    private LinkedBlockingDeque<Status> queue = null;

    private SpoutOutputCollector _collector;

    public TwitterSampleSpout(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret, String[] keywords){
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.keywords = keywords;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;

        queue = new LinkedBlockingDeque<>(1000);

        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                queue.add(status);
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {

            }

            @Override
            public void onStallWarning(StallWarning warning) {

            }

            @Override
            public void onException(Exception ex) {

            }
        };

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);

        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        twitterStream.addListener(listener);

        if (keywords.length == 0){
            twitterStream.sample();
        } else {
            FilterQuery query = new FilterQuery().track(keywords);
            twitterStream.filter(query);
        }

    }

    @Override
    public void nextTuple() {
        Status ret = queue.poll();

        if (ret == null){
            Utils.sleep(50);
        } else {
            String tweet = ret.getText();
            _collector.emit(new Values(tweet));
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("tweet"));
    }


    @Override
    public void close() {
        super.close();
        twitterStream.shutdown();
    }
}
