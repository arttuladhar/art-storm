package com.aayushtuladhar.storm.topology;

import com.aayushtuladhar.storm.bolt.PrintBolt;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.testing.TestWordSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloWorldTopology {

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {

        //Creating Topology
        TopologyBuilder builder = new TopologyBuilder();

        //Attaching Spout
        builder.setSpout("word", new TestWordSpout(), 10);

        //Attaching Bolt
        builder.setBolt("printWord", new PrintBolt(), 2).shuffleGrouping("word");

        //Creating Default Config Object
        Config conf = new Config();


        if (args != null && args.length > 0) {
            //Running in Live Cluster
            conf.setNumWorkers(3);
            conf.setDebug(false);
            try {
                log.info("Submitting " + args[0]);
                StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
            } catch (AuthorizationException e) {
                e.printStackTrace();
            }

        } else {
            // create the local cluster instance
            LocalCluster cluster = new LocalCluster();
            conf.setDebug(true);

            log.info("Submitting HelloWorld Topology in Local Cluster");
            // submit the topology to the local cluster
            cluster.submitTopology("HelloWorldTopology", conf, builder.createTopology());
            Utils.sleep(1000 * 30);

            // kill the topology
            cluster.killTopology("HelloWorldTopology");

            // we are done, so shutdown the local cluster
            cluster.shutdown();
            log.info("Topology Killed");
        }

    }

}
