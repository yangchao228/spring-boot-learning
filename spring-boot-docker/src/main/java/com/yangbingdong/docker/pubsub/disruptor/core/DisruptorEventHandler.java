package com.yangbingdong.docker.pubsub.disruptor.core;

/**
 * @author ybd
 * @date 18-5-4
 * @contact yangbingdong1994@gmail.com
 */
public interface DisruptorEventHandler<T extends AbstractDisruptorEvent> {
	int order();

	void onEvent(T event, long sequence, boolean endOfBatch, int currentShard) throws Exception;

	default boolean enableSharding(){
		return false;
	}

	default int shardingQuantity(){
		return 1;
	}
}