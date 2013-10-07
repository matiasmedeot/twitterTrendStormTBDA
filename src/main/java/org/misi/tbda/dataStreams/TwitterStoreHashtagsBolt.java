package org.misi.tbda.dataStreams;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

public class TwitterStoreHashtagsBolt extends BaseBasicBolt{

	private static final long serialVersionUID = 1L;
	Jedis jedis;
	
	@Override
	public void cleanup() {
		
	}
 
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String hashtag = (String) input.getValueByField("hashtag");
		String hashtagFrecuency = (String) input.getValueByField("frequencyValue");
		jedis.set(hashtag, hashtagFrecuency);
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
	 jedis = new Jedis("127.0.0.1",6379);	
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}
