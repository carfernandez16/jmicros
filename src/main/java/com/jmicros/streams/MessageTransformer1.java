package com.jmicros.streams;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class MessageTransformer1 implements TransformerSupplier<String, String, KeyValue<String, String>> {

    private static final Logger logger = LogManager.getLogger(MessageTransformer1.class);

    @Override
    public Transformer<String, String, KeyValue<String, String>> get() {
        return new Transformer<String, String, KeyValue<String, String>>() {

            ProcessorContext context;

            @Override
            public void init(ProcessorContext processorContext) {
                this.context = processorContext;
            }

            @Override
            public KeyValue<String, String> transform(String key, String message) {
                String newMessage = processMessage(key, message, context);
                if(newMessage != null){
                    logger.info("key: " + key + ", newMessage: " + newMessage + ", topic: " + context.topic());
                    return KeyValue.pair(key, newMessage);
                }
                return KeyValue.pair(key, message);
            }

            @Override
            public void close() {}
        };
    }

    private String processMessage(String key, String message, ProcessorContext context) {
        JSONObject aggregatedMessage = messageToJson(message);
        if(aggregatedMessage != null){
            try {
                JSONObject rangeTemperature = getRangeTemperature();
                aggregatedMessage.put("rangeTemperature", rangeTemperature);
                JSONObject rangeHumidity = getRangeHumidity();
                aggregatedMessage.put("rangeHumidity", rangeHumidity);
            } catch (JSONException e) {
                logger.error("Cannot transforme message="+ message, e);
            }
            return aggregatedMessage.toString();
        }
        return null;
    }

    private JSONObject messageToJson(String message){
        JSONObject msg = null;
        try {
            msg = new JSONObject(message);
        } catch (JSONException e) {
            logger.error("Cannot parse message=" + message, e);
        }
        logger.info("nsg=" + msg);
        return msg;
    }

    private JSONObject getRangeHumidity() {
        JSONObject rangeHumidity = new JSONObject();
        try {
            rangeHumidity.put("unit","%");
            rangeHumidity.put("min",20);
            rangeHumidity.put("max",90);
        } catch (JSONException e) {
            logger.error("Cannot get range temperature", e);
        }
        return rangeHumidity;
    }

    private JSONObject getRangeTemperature() {
        JSONObject rangeTemperature = new JSONObject();
        try {
            rangeTemperature.put("unit","C");
            rangeTemperature.put("min",0);
            rangeTemperature.put("max",50);
        } catch (JSONException e) {
            logger.error("Cannot get range temperature", e);
        }
        return rangeTemperature;
    }
}
