package com.vetautet.ddd.infrastructure.cache.local;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.vetautet.ddd.domain.models.entities.TicketDetail;
import com.vetautet.ddd.domain.repositories.TicketDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketLocalCacheService {

    final TicketDetailRepository ticketDetailRepository;

    private final Cache<Long, TicketDetail> ticketLocalCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats() // Bật ghi nhận thống kê
            .build();


    public TicketDetail getTicket(Long ticketId) {
        try {
            // Lấy từ cache, nếu không có sẽ load từ database
            return ticketLocalCache.get(ticketId, () -> null);
        } catch (Exception e) {
            return null;
        }
    }

    public void setTicketDetail(TicketDetail ticketDetail) {
        if (ticketDetail == null) {
            return;
        }
        ticketLocalCache.put(ticketDetail.getId(), ticketDetail);
    }

    private TicketDetail loadTicketFromDB(Long ticketId) {
        // Logic load từ database
        return ticketDetailRepository.findById(ticketId).orElse(null);
    }

    public void invalidate(Long ticketId) {
        ticketLocalCache.invalidate(ticketId);
    }

    public void reportCache() {
        // Lấy thống kê
        CacheStats stats = ticketLocalCache.stats();
        System.out.println("Hit rate: " + stats.hitRate());
        System.out.println("Miss rate: " + stats.missRate());
        System.out.println("Load success count: " + stats.loadSuccessCount());
    }
}
