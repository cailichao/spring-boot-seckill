package com.itstyle.seckill.web;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itstyle.seckill.common.entity.Result;
@Api(tags ="抢红包")
@RestController
@RequestMapping("/redPacket")
public class RedPacketController {
	
	
	@ApiOperation(value="抢红包一(最low实现)",nickname="科帮网")
	@PostMapping("/start")
	public static Result start(long seckillId){
		return Result.ok();
	}
}