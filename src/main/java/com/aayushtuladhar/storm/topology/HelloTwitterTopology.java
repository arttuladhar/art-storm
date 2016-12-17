package com.aayushtuladhar.storm.topology;


import com.aayushtuladhar.storm.bolt.PrintBolt;
import com.aayushtuladhar.storm.spout.TwitterSampleSpout;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

public class HelloTwitterTopology {

    private static String TWITTER_CONSUMER_KEY = "";
    private static String TWITTER_CONSUMER_SECRET = "";

    private static String TWITTER_ACCESS_TOKEN = "";
    private static String TWITTER_ACCESS_SECRET = "";

    public static void main(String[] args) {
        System.out.println("Starting Hello World Topology");
        String[] keywords = {"Nepal"};

        TopologyBuilder builder = new TopologyBuilder();

        //Setting Spout
        builder.setSpout("twitterTweets", new TwitterSampleSpout(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, TWITTER_ACCESS_TOKEN, TWITTER_ACCESS_SECRET, keywords));

        //Setting Bolt
        builder.setBolt("printTweets", new PrintBolt()).shuffleGrouping("twitterTweets");

        Config conf = new Config();

        if (args != null && args.length > 0) {
            conf.setNumWorkers(3);
            conf.setDebug(false);

            try {
                StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
            } catch (InvalidTopologyException e) {
                e.printStackTrace();
            } catch (AlreadyAliveException e) {
                e.printStackTrace();
            } catch (AuthorizationException e) {
                e.printStackTrace();
            }

        } else {

            LocalCluster cluster = new LocalCluster();
            conf.setDebug(true);

            // submit the topology to the local cluster
            cluster.submitTopology("HelloTwitterTopology", conf, builder.createTopology());

            System.out.println("Running HelloTwitter Topology");
            Utils.sleep(1000 * 60 * 5);

            // kill the topology
            cluster.killTopology("HelloWorldTopology");

            // we are done, so shutdown the local cluster
            cluster.shutdown();
        }
    }
}
