package io.gravitee.resource.cache.rediscache;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.util.TimeUtil;


public class RedisElement implements Serializable, Cloneable {
	private static final long serialVersionUID = 1098572221246444544L;
	
	private final Object key;
	 
	private final Object value;
	
	private volatile int timeToLive = Integer.MIN_VALUE;
	
	private volatile int timeToIdle = Integer.MIN_VALUE;
	
	private transient long creationTime;
	
	private transient long lastAccessTime;
	
	private volatile long lastUpdateTime;
	
	private volatile boolean cacheDefaultLifespan = true;
	
	private static final long NOT_SET_ID = 0;
	
	private volatile long id = NOT_SET_ID;
	
	public RedisElement(final Object key, final Object value, final int timeToIdleSeconds, final int timeToLiveSeconds) {
		 this.key = key;
		 this.value = value;
		 setTimeToIdle(timeToIdleSeconds);
		 setTimeToLive(timeToLiveSeconds);
	}
	
	public final Object getObjectKey() {
        return key;
    }

	public final Object getObjectValue() {
        return value;
    }
	
	@Override
    public final boolean equals(final Object object) {
        if (object == null || !(object instanceof RedisElement)) {
            return false;
        }

        RedisElement element = (RedisElement) object;
        if (key == null || element.getObjectKey() == null) {
            return false;
        }

        return key.equals(element.getObjectKey());
    }
	
	public void setTimeToLive(final int timeToLiveSeconds) {
        if (timeToLiveSeconds < 0) {
            throw new IllegalArgumentException("timeToLive can't be negative");
        }
        this.cacheDefaultLifespan = false;
        this.timeToLive = timeToLiveSeconds;
    }
	
	public void setTimeToIdle(final int timeToIdleSeconds) {
        if (timeToIdleSeconds < 0) {
            throw new IllegalArgumentException("timeToIdle can't be negative");
        }
        this.cacheDefaultLifespan = false;
        this.timeToIdle = timeToIdleSeconds;
    }
	
	@Override
    public final int hashCode() {
        return key.hashCode();
    }
	
	void setId(final long id) {
        if (id == NOT_SET_ID) {
            throw new IllegalArgumentException("Id cannot be set to " + id);
        }
        this.id = id;
    }
	
	long getId() {
        final long v = id;
        if (v == NOT_SET_ID) {
            throw new IllegalStateException("Id not set");
        }
        return v;
    }
	
	boolean hasId() {
        return id != NOT_SET_ID;
    }
	
	@Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[ key = ").append(key)
                .append(", value=").append(value)
                .append(" ]");

        return sb.toString();
    }
	
	public final boolean isSerializable() {
        return isKeySerializable()
            && (value instanceof Serializable || value == null);
    }
	
	public final boolean isKeySerializable() {
        return key instanceof Serializable || key == null;
    }
	
	public boolean isExpired() {
        if (!isLifespanSet() || isEternal()) {
            return false;
        }

        long now = getCurrentTime();
        long expirationTime = getExpirationTime();

        return now > expirationTime;
    }
	
	public boolean isExpired(CacheConfiguration config) {
        if (cacheDefaultLifespan) {
            if (config.isEternal()) {
                timeToIdle = 0;
                timeToLive = 0;
            } else {
                timeToIdle = TimeUtil.convertTimeToInt(config.getTimeToIdleSeconds());
                timeToLive = TimeUtil.convertTimeToInt(config.getTimeToLiveSeconds());
            }
        }
        return isExpired();
    }
	
	public long getExpirationTime() {
        if (!isLifespanSet() || isEternal()) {
            return Long.MAX_VALUE;
        }

        long expirationTime = 0;
        long ttlExpiry = creationTime + TimeUtil.toMillis(getTimeToLive());

        long mostRecentTime = Math.max(creationTime, lastAccessTime);
        long ttiExpiry = mostRecentTime + TimeUtil.toMillis(getTimeToIdle());

        if (getTimeToLive() != 0 && (getTimeToIdle() == 0 || lastAccessTime == 0)) {
            expirationTime = ttlExpiry;
        } else if (getTimeToLive() == 0) {
            expirationTime = ttiExpiry;
        } else {
            expirationTime = Math.min(ttlExpiry, ttiExpiry);
        }
        return expirationTime;
    }
	
	public boolean isEternal() {
        return (0 == timeToIdle) && (0 == timeToLive);
    }
	
	public void setEternal(final boolean eternal) {
        if (eternal) {
            this.cacheDefaultLifespan = false;
            this.timeToIdle = 0;
            this.timeToLive = 0;
        } else if (isEternal()) {
            this.cacheDefaultLifespan = false;
            this.timeToIdle = Integer.MIN_VALUE;
            this.timeToLive = Integer.MIN_VALUE;
        }
    }
	
	public boolean isLifespanSet() {
        return this.timeToIdle != Integer.MIN_VALUE || this.timeToLive != Integer.MIN_VALUE;
    }
	
	public int getTimeToLive() {
        if (Integer.MIN_VALUE == timeToLive) {
            return 0;
        } else {
            return timeToLive;
        }
    }
	
	public int getTimeToIdle() {
        if (Integer.MIN_VALUE == timeToIdle) {
            return 0;
        } else {
            return timeToIdle;
        }
    }
	
	public boolean usesCacheDefaultLifespan() {
        return cacheDefaultLifespan;
    }
	
    protected void setLifespanDefaults(int tti, int ttl, boolean eternal) {
        if (eternal) {
            this.timeToIdle = 0;
            this.timeToLive = 0;
        } else if (isEternal()) {
            this.timeToIdle = Integer.MIN_VALUE;
            this.timeToLive = Integer.MIN_VALUE;
        } else {
            timeToIdle = tti;
            timeToLive = ttl;
        }
    }
    
    long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(TimeUtil.toSecs(creationTime));
        out.writeInt(TimeUtil.toSecs(lastAccessTime));
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        creationTime = TimeUtil.toMillis(in.readInt());
        lastAccessTime = TimeUtil.toMillis(in.readInt());
    }
}
