package org.redisson;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.reactivestreams.Publisher;
import org.redisson.core.RCollectionReactive;

import reactor.rx.Streams;

public abstract class BaseReactiveTest {

    protected static RedissonReactiveClient redisson;

    @BeforeClass
    public static void beforeClass() {
        redisson = createInstance();
    }

    @AfterClass
    public static void afterClass() {
        redisson.shutdown();
    }

    public <V> Iterable<V> sync(RCollectionReactive<V> list) {
        return Streams.create(list.iterator()).toList().poll();
    }

    public <V> Iterator<V> toIterator(Publisher<V> pub) {
        return Streams.create(pub).toList().poll().iterator();
    }

    public <V> V sync(Publisher<V> ob) {
        List<V> t = Streams.create(ob).toList().poll();
        if (t == null) {
            return null;
        }
        return t.iterator().next();
    }

    public static Config createConfig() {
        String redisAddress = System.getProperty("redisAddress");
        if (redisAddress == null) {
            redisAddress = "127.0.0.1:6379";
        }
        Config config = new Config();
        config.useSingleServer().setAddress(redisAddress);
        return config;
    }

    public static RedissonReactiveClient createInstance() {
        Config config = createConfig();
        return Redisson.createReactive(config);
    }

    @After
    public void after() {
        redisson.flushdb();
    }

}
