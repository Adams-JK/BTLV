package com.zykj.btlv.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @AUTHOR 风间彻
 * @TIME: 2022-04-2022/4/25 15:51
 * @DESCRIPTION: redis工具类
 **/
@Slf4j
@Component
public class RedisTool {

    @Autowired
    private RedisTemplate redisTemplate;

    public RedisTool(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 指定缓存失效时间
     */
    public boolean setTime(String key, long time, int index) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     */
    public long getTime(String key, int index) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     */
    public boolean exist(String key, int index) {
        try {

            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     */
    public void delete(int index, String... key) {

        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    //============================String=============================

    /**
     * 获取string
     */
    public String getString(String key, int index) {
        if(key == null || redisTemplate.opsForValue().get(key) == null){
            return null;
        }
        return redisTemplate.opsForValue().get(key).toString();
    }

    /**
     * 设置string
     */
    public boolean setString(String key, String value, Long time, Integer index) {
        try {

            redisTemplate.opsForValue().set(key, value);
            if (time != null) {
                setTime(key, time, index);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //================================HASH=================================

    /**
     * 获取hash
     */
    public Map<Object, Object> getHash(String key, int index) {

        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 设置hash
     */
    public boolean setHash(String key, Map<String, Object> map, Long time, int index) {
        try {

            redisTemplate.opsForHash().putAll(key, map);
            if (time != null) {
                setTime(key, time, index);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //================================LIST=================================
    public boolean leftPush(String key, Map<String, Object> map, Long time, int index) {
        try {

            redisTemplate.opsForList().leftPush(key, map);
            if (time != null) {
                setTime(key, time, index);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List rang(String key, Long start, Long end, int index) {
        try {

            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ArrayList();
        }
    }

    public void addC2cNotice(String key, String value) {
        // 将记录推入列表的左侧
        redisTemplate.opsForList().leftPush(key, value);

        // 保留最新的30条记录，删除列表中多余的记录
        trimList(key, 0, 29);
    }

    private void trimList(String key, long start, long end) {
        // 使用 trim 命令保留指定范围内的记录
        redisTemplate.opsForList().trim(key, start, end);
    }

    public void addGameOut(String key, String value) {
        // 将记录推入列表的左侧
        redisTemplate.opsForList().leftPush(key, value);

        // 保留最新的30条记录，删除列表中多余的记录
        trimList(key, 0, 99);
    }


    // 创建订单并设置过期时间
    public void createOrder(String key, String orderId, int expirationTimeInSeconds) {
        redisTemplate.opsForSet().add(key, orderId);
        redisTemplate.opsForValue().set(key+orderId,"xx",expirationTimeInSeconds, TimeUnit.SECONDS);
    }

}
