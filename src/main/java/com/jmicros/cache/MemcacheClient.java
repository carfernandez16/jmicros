package com.jmicros.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.InetSocketAddress;
import net.spy.memcached.MemcachedClient;

public class MemcacheClient {

    private static Logger logger = LogManager.getLogger(MemcacheClient.class);

    public static void main(String args[]) throws IOException {
        // Connecting to Memcached server on localhost
        MemcachedClient mcc = new MemcachedClient(new
                InetSocketAddress("127.0.0.1", 11212));
        System.out.println("Connection to server sucessfully");

        //not set data into memcached server
        System.out.println("set status:"+mcc.set("tutorialspoint", 900, "memcached").isDone());

        //Get value from cache
        System.out.println("Get from Cache:"+mcc.get("tutorialspoint"));
    }

}
