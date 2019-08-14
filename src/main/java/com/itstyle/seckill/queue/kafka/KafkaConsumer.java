package com.itstyle.seckill.queue.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.itstyle.seckill.common.entity.Result;
import com.itstyle.seckill.common.redis.RedisUtil;
import com.itstyle.seckill.common.webSocket.WebSocketServer;
import com.itstyle.seckill.service.ISeckillService;
/**
 * 消费者 spring-kafka 2.0 + 依赖JDK8
 * @author 科帮网 By https://blog.52itstyle.com
 */
@Component
public class KafkaConsumer {
	@Autowired
	private ISeckillService seckillService;
	
	private static RedisUtil redisUtil = new RedisUtil();
    /**
     * 监听seckill主题,有消息就读取
     * @param message
     */
    @KafkaListener(topics = {"seckill"})
    public void receiveMessage(String message){
    	//收到通道的消息之后执行秒杀操作
    	String[] array = message.split(";"); 
    	if(redisUtil.getValue(array[0])==null){//control层已经判断了，其实这里不需要再判断了，这个接口有限流 注意一下
    		Result result = seckillService.startSeckil(Long.parseLong(array[0]), Long.parseLong(array[1]));
    		//可以注释掉上面的使用这个测试
    	    //Result result = seckillService.startSeckilDBPCC_TWO(Long.parseLong(array[0]), Long.parseLong(array[1]));
    		if(result.equals(Result.ok())){
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