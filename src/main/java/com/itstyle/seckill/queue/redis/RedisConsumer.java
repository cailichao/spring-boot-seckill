package com.itstyle.seckill.queue.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itstyle.seckill.common.entity.Result;
import com.itstyle.seckill.common.enums.SeckillStatEnum;
import com.itstyle.seckill.common.redis.RedisUtil;
import com.itstyle.seckill.common.webSocket.WebSocketServer;
import com.itstyle.seckill.service.ISeckillService;
/**
 * 消费者
 * @author 科帮网 By https://blog.52itstyle.com
 */
@Service
public class RedisConsumer {
	
	@Autowired
	private ISeckillService seckillService;
	@Autowired
	private RedisUtil redisUtil;
	
    public void receiveMessage(String message) {
        //收到通道的消息之后执行秒杀操作(超卖)
    	String[] array = message.split(";"); 
    	if(redisUtil.getValue(array[0])==null){//control层已经判断了，其实这里不需要再判断了
    		Result result = seckillService.startSeckilDBPCC_TWO(Long.parseLong(array[0]), Long.parseLong(array[1]));
    		if(result.equals(Result.ok(SeckillStatEnum.SUCCESS))){
    			WebSocketServer.sendInfo(array[0].toString(), "秒杀成功");//推送给前台
    		}else{
    			WebSocketServer.sendInfo(array[0].toString(), "秒杀失败");//推送给前台
    			redisUtil.cacheValue(array[0], "ok");//秒杀结束
    		}
    	}else{
    		WebSocketServer.sendInfo(array[0].toString(), "秒杀失败");//推送给前台
    	}
    }
}