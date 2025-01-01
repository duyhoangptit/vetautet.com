package com.vetautet.ddd.application.services.gif;

import com.vetautet.ddd.domain.models.entities.Gift;
import com.vetautet.ddd.domain.repositories.GiftRepository;
import com.vetautet.ddd.infrastructure.distributed.redisson.RedisDistributedLocker;
import com.vetautet.ddd.infrastructure.distributed.redisson.RedisDistributedService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class GiftService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private RedisDistributedService redisDistributedService;

    private static final String GIFT_KEY_PREFIX = "gift:";
    private static final String GIFT_KEY = "gift:remaining";
    private static final String LOCK_KEY = "gift:lock";

    // TTL cho Redis (ví dụ: 10 phút)
    private static final long CACHE_TTL = 10 * 60;

    /**
     * Lấy số lượng quà còn lại (ưu tiên đọc từ Redis)
     */
    public int getRemainingGifts(Long giftId) {
        String redisKey = GIFT_KEY_PREFIX + giftId;
        log.info("getRemainingGifts {}", redisKey);

        // Kiểm tra Redis trước
        String cachedValue = redisTemplate.opsForValue().get(redisKey);
        if (cachedValue != null) {
            // Nếu Redis có dữ liệu, trả về
            log.info("getRemainingGifts get from CACHE 1 {} {}", redisKey, cachedValue);
            return Integer.parseInt(cachedValue);
        }

        // Tao lock process voi KEY
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock("PRO_LOCK_KEY_ITEM" + giftId);
        try {
            if (!locker.tryLock(2, 5, TimeUnit.SECONDS)) {
                redisTemplate.opsForValue().set(redisKey, String.valueOf(0), CACHE_TTL, TimeUnit.SECONDS);
                return 0;
            }

            cachedValue = redisTemplate.opsForValue().get(redisKey);
            if (cachedValue != null) {
                // Nếu Redis có dữ liệu, trả về
                log.info("getRemainingGifts get from CACHE 2 {} {}", redisKey, cachedValue);
                return Integer.parseInt(cachedValue);
            }

            // Nếu Redis không có dữ liệu, đọc từ DB
            Optional<Gift> giftOptional = giftRepository.findById(giftId);
            if (giftOptional.isPresent()) {
                int remainingQuantity = giftOptional.get().getRemainingQuantity();
                log.info("getRemainingGifts get from DB {}", remainingQuantity);

                // Đồng bộ lại vào Redis với TTL
                redisTemplate.opsForValue().set(redisKey, String.valueOf(remainingQuantity), CACHE_TTL, TimeUnit.SECONDS);

                return remainingQuantity;
            }

            log.info("getRemainingGifts error {}", redisKey);
            // Nếu không tìm thấy trong DB, trả về 0 hoặc xử lý lỗi
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public boolean claimGiftVip(Long giftId) {
        String redisKey = GIFT_KEY_PREFIX + giftId;

        // Sử dụng Redis để giảm số lượng quà
        int remainingQuantity = getRemainingGifts(giftId);
        log.info("claimGiftVip remainingQuantity {}", remainingQuantity);
        Long remainingGifts = redisTemplate.opsForValue().decrement(redisKey);
        log.info("claimGiftVip remainingGifts {}", remainingGifts);

        if (remainingGifts != null && remainingGifts >= 0) {
            log.info("claimGiftVip value {}", remainingGifts.intValue());
            // Nếu giảm thành công trong Redis, cập nhật vào DB
            giftRepository.findById(giftId).ifPresent(gift -> {
                gift.setRemainingQuantity(remainingGifts.intValue());
                giftRepository.save(gift);
            });
            log.info("claimGiftVip successful");
            return true;
        }

        log.info("claimGiftVip fail");
        // Nếu số lượng quà âm, rollback Redis
        redisTemplate.opsForValue().increment(redisKey);
        return false;
    }

    public boolean claimGift() {
        RLock lock = redissonClient.getLock(LOCK_KEY);

        try {
            // Thử lấy khóa với thời gian chờ và thời gian hết hạn
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                // Giảm số lượng quà trong Redis
                Long remainingGifts = redisTemplate.opsForValue().decrement(GIFT_KEY);

                if (remainingGifts == null || remainingGifts < 0) {
                    // Nếu số lượng quà âm, từ chối yêu cầu và hoàn tác
                    redisTemplate.opsForValue().increment(GIFT_KEY); // Rollback
                    return false;
                }

                // Nếu số lượng quà hợp lệ, chấp nhận yêu cầu
                log.info("claimGift success");
                return true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Giải phóng khóa
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return false;
    }
}