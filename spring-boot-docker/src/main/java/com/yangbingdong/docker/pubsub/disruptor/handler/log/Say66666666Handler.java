package com.yangbingdong.docker.pubsub.disruptor.handler.log;

import com.yangbingdong.docker.aop.Sharding;
import com.yangbingdong.docker.pubsub.disruptor.core.DisruptorEventHandler;
import com.yangbingdong.docker.pubsub.disruptor.event.log.AccessLogEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author ybd
 * @date 18-5-4
 * @contact yangbingdong1994@gmail.com
 */
@Component
@Slf4j
@Sharding(3)
public class Say66666666Handler implements DisruptorEventHandler<AccessLogEvent> {

	@Override
	public int order() {
		return Integer.MIN_VALUE;
	}

	@Override
	public void onEvent(AccessLogEvent event, long sequence, boolean endOfBatch, int currentShard) throws Exception {
		log.info("6666666666666666666666666! sequence: {}, current shard: {}", sequence, currentShard);
	}
}
