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
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class TwitterSumarizeHashtagsBolt extends BaseBasicBolt{

	private static final long serialVersionUID = 1L;
	Map<String, Integer> hashtags = new HashMap<String, Integer>();
	Jedis jedis;
	
	@Override
	public void cleanup() {
		
	}
 
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		JSONObject json = (JSONObject)input.getValueByField("tweet");
		if(json.containsKey("entities")){
			JSONObject entities = (JSONObject) json.get("entities");
				if(entities.containsKey("hashtags")){
					for(Object hashObj : (JSONArray)entities.get("hashtags")){
						JSONObject hashJson = (JSONObject)hashObj;
						String hash = hashJson.get("text").toString().toLowerCase();
						Integer frequencyValue = 1;
						if(!hashtags.containsKey(hash)){
							hashtags.put(hash, frequencyValue);
						}else{
							Integer last = hashtags.get(hash);
							frequencyValue = last + 1;
							hashtags.put(hash,frequencyValue);
						}	
						collector.emit(new Values(hash,frequencyValue.toString())); 
					}
			}
		}
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		jedis = new Jedis("127.0.0.1",6379);

		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				Map<String, Integer> oldMap = new HashMap<String, Integer>(hashtags);
				hashtags.clear();
				for(Map.Entry<String, Integer> entry : oldMap.entrySet()){
					System.out.println(entry.getKey()+": "+entry.getValue());
				}
			}
			
		};
		Timer t = new Timer();
		t.scheduleAtFixedRate(task, 60000, 60000);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("hashtag", "frequencyValue"));
	}

}
