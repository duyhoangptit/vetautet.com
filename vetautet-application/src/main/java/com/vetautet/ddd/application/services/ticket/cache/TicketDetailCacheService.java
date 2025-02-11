package com.vetautet.ddd.application.services.ticket.cache;

import com.vetautet.ddd.application.models.cache.TicketDetailCache;
import com.vetautet.ddd.domain.models.entities.TicketDetail;
import com.vetautet.ddd.domain.services.TicketDetailDomainService;
import com.vetautet.ddd.infrastructure.cache.local.grava.TicketLocalCacheService;
import com.vetautet.ddd.infrastructure.cache.redis.RedisInfrasService;
import com.vetautet.ddd.infrastructure.distributed.redisson.RedisDistributedLocker;
import com.vetautet.ddd.infrastructure.distributed.redisson.RedisDistributedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TicketDetailCacheService {

    @Autowired
    private RedisDistributedService redisDistributedService;
    @Autowired // Khai bao cache
    private RedisInfrasService redisInfrasService;
    @Autowired
    private TicketDetailDomainService ticketDetailDomainService;
    @Autowired
    private TicketLocalCacheService ticketLocalCacheService;

    public boolean orderTicketByUser(Long ticketId) {
        ticketLocalCacheService.invalidate(ticketId);
        redisInfrasService.removeObject(genEventItemKey(ticketId));
        return true;
    }

    public TicketDetailCache getTicketDetail(Long ticketId, Long version) {
        TicketDetailCache ticketDetailCache = getTicketDetailFromLocalCache(ticketId);

        if (ticketDetailCache != null) {
            // version null
            if (version == null) {
                return ticketDetailCache;
            }

            // version = version on cache
            if (version.equals(ticketDetailCache.getVersion())) {
                return ticketDetailCache;
            }

            // version < version cache
            if (version < ticketDetailCache.getVersion()) {
                return ticketDetailCache;
            }

            // version > version cache
            return getTicketDetailFromDistributedCache(ticketId);

        }

        return getTicketDetailFromDistributedCache(ticketId);
    }

    public TicketDetailCache getTicketDetailFromLocalCache(Long ticketId) {
        return new TicketDetailCache();
    }

    public TicketDetailCache getTicketDetailFromDistributedCache(Long ticketId) {
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock("PRO_LOCK_KEY_ITEM" + ticketId);
        try {
            // 1 - Tao lock
            boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);
            if (!isLock) {
                return null;
            }

            // Get cache
            TicketDetailCache ticketDetailCache = redisInfrasService.getObject(genEventItemKey(ticketId), TicketDetailCache.class);
            // 2. YES
            if (ticketDetailCache != null) {
                return ticketDetailCache;
            }

            // 3 -> van khong co thi truy van DB
            TicketDetail ticketDetail = ticketDetailDomainService.getTicketDetailById(ticketId);
            ticketDetailCache = new TicketDetailCache().withTicketDetail(ticketDetail).withVersion(System.currentTimeMillis());

            // neu co thi set redis
            redisInfrasService.setObject(genEventItemKey(ticketId), ticketDetailCache, 10, TimeUnit.MINUTES); // TTL

            return ticketDetailCache;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public TicketDetail getTicketDetailCacheClean(Long id, Long version) {
        log.info("Implement getTicketDetailCacheClean->, {}, {} ", id, version);

        // 1. get from local cache
        // compare version
        TicketDetail ticketDetail = getTicketDetailLocalCache(id);
        if (ticketDetail != null) {
            log.info("FROM LOCAL CACHE {}, {}, {}", id, version, ticketDetail);
            return ticketDetail;
        }

        // 2. Redis cache
        return getTicketDetailDistributedCache(id, version);
    }

    private TicketDetail getTicketDetailLocalCache(Long id) {
        TicketDetail ticketDetail = ticketLocalCacheService.getTicket(id);
        return ticketDetail;
    }

    private TicketDetail getTicketDetailDistributedCache(Long id, Long version) {
        TicketDetail ticketDetail;
        ticketDetail = redisInfrasService.getObject(genEventItemKey(id), TicketDetail.class);
        if (ticketDetail != null) {
            log.info("FROM CACHE EXIST {}", ticketDetail);
            ticketLocalCacheService.setTicketDetail(ticketDetail);
            return ticketDetail;
        }

        // 3. lock record
        log.info("CACHE NO EXIST, START GET DB AND SET CACHE->, {}, {} ", id, version);
        // Tao lock process voi KEY
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock("PRO_LOCK_KEY_ITEM" + id);
        try {
            // 1 - Tao lock
            boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);
            if (!isLock) {
                log.info("LOCK WAIT ITEM PLEASE....{}", version);
                return ticketDetail;
            }

            // Get cache
            ticketDetail = redisInfrasService.getObject(genEventItemKey(id), TicketDetail.class);
            // 2. YES
            if (ticketDetail != null) {
                log.info("FROM CACHE :: {}, {}, {}", id, version, ticketDetail);
                ticketLocalCacheService.setTicketDetail(ticketDetail);
                return ticketDetail;
            }

            // 3 -> van khong co thi truy van DB
            ticketDetail = ticketDetailDomainService.getTicketDetailById(id);
            log.info("FROM DBS ->>>> {}, {}", ticketDetail, version);
            if (ticketDetail == null) { // Neu trong dbs van khong co thi return ve not exists;
                log.info("TICKET NOT EXITS....{}", version);
                // set
                redisInfrasService.setObject(genEventItemKey(id), ticketDetail, 10, TimeUnit.MINUTES);
                return ticketDetail;
            }

            // neu co thi set redis
            redisInfrasService.setObject(genEventItemKey(id), ticketDetail, 10, TimeUnit.MINUTES); // TTL
            // local cache
            ticketLocalCacheService.setTicketDetail(ticketDetail);

            return ticketDetail;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    private String genEventItemKey(Long itemId) {
        return "PRO_TICKET:ITEM:" + itemId;
    }
}
