package com.jmicros.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.spy.memcached.MemcachedClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Path("/jmicros-api/rest/jugbol")
@Produces(MediaType.TEXT_PLAIN)
public class MessageResource {

    private static Logger logger = LogManager.getLogger(MessageResource.class);

    private Map<Integer, Long> primosMap;
    private LoadingCache<Integer, Long> cache;
    private MemcachedClient memcachedClient;

    public MessageResource(){
        initMapCache();
        initGuavaCache();
        initMemcache();
    }

    private void initMemcache() {
        memcachedClient = null;
        try {
            memcachedClient = new MemcachedClient(new
                    InetSocketAddress("memcache", 11211));
        } catch (IOException e) {
            logger.error("no se puede conectar con el servidor de memcache", e);
        }
    }

    private void initMapCache() {
        primosMap = new HashMap<>();
    }

    private void initGuavaCache() {
        CacheLoader<Integer, Long> loader = new CacheLoader<Integer, Long>(){
            @Override
            public Long load(Integer key) {
                logger.info("calling service....");
                PrimoService primoService = new PrimoService();
                return primoService.getPrimo(key);
            }
        };
        cache = CacheBuilder.newBuilder().expireAfterAccess(24, TimeUnit.HOURS).build(loader);
    }

    @GET
    @Path("/{param}")
    public Response genPrimo(@PathParam("param") String param) {
        logger.info("***primo a generar=" + param);
        int nsimoPrimo = Integer.parseInt(param);
        PrimoService primoService = new PrimoService();
        long primo = primoService.getPrimo(nsimoPrimo);
        return Response.status(200).entity(primo).build();
    }

    @GET
    @Path("/map/{param}")
    public Response genPrimoMap(@PathParam("param") String param) {
        logger.info("***[MAP] primo a generar=" + param);
        int nsimoPrimo = Integer.parseInt(param);

        long primo;
        if(!primosMap.containsKey(nsimoPrimo)) {
            PrimoService primoService = new PrimoService();
            primo = primoService.getPrimo(nsimoPrimo);
            primosMap.put(nsimoPrimo, primo);
            logger.info("Primo calculado...");
        }else{
            primo = primosMap.get(nsimoPrimo);
            logger.info("Primo recuperado de cache...");
        }
        return Response.status(200).entity(primo).build();
    }

    @GET
    @Path("/guava/{param}")
    public Response genPrimoGuava(@PathParam("param") String param) {
        logger.info("***[GUAVA] primo a generar=" + param);
        int nsimoPrimo = Integer.parseInt(param);
        long primo = -1;
        try {
            primo = cache.get(nsimoPrimo);
        } catch (ExecutionException e) {
            logger.error("no se puede obtener primo", e);
            return Response.status(500).entity(primo).build();
        }
        return Response.status(200).entity(primo).build();
    }

    @GET
    @Path("/memcache/{param}")
    public Response genPrimoMemcache(@PathParam("param") String param) {
        logger.info("***[memcache] primo a generar=" + param);
        int nsimoPrimo = Integer.parseInt(param);

        Long primo = (Long) memcachedClient.get(param);
        if(primo == null) {
            PrimoService primoService = new PrimoService();
            primo = primoService.getPrimo(nsimoPrimo);
            memcachedClient.set(param, 900, primo);
            logger.info("Primo calculado...");
        }

        return Response.status(200).entity(primo).build();
    }


}
