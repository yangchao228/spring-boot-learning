package com.yangbingdong.springbootdisruptor.basic;


import com.lmax.disruptor.EventTranslatorOneArg;

import java.nio.ByteBuffer;

/**
 * @author ybd
 * @date 18-1-31
 * @contact yangbingdong@1994.gmail
 */
public class LongEventProducerWithTranslator implements EventTranslatorOneArg<LongEvent, ByteBuffer>{
	@Override
	public void translateTo(LongEvent event, long sequence, ByteBuffer bb) {
		event.setValue(bb.getLong(0));
	}
}
