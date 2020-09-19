package com.jmicros.rest;
import com.jmicros.cache.MessageResource;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class FatJarApplication extends Application {

    public FatJarApplication() {}

    @Override
    public Set<Object> getSingletons() {
        HashSet<Object> set = new HashSet<Object>();
        set.add(new MessageResource());
        return set;
    }
}