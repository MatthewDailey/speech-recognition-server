package com.saypenis.speech.api.read;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.saypenis.speech.api.serialization.RoundBean;
import com.saypenis.speech.aws.AwsSupplier;
import com.saypenis.speech.aws.SayPenisConfiguration;

@SuppressWarnings("unused")
@Singleton
public final class PagingCaches {
	
	private static Logger log = LoggerFactory.getLogger(PagingCaches.class);
	
	private static final String dynamoUserDateIndex = "user_id-date-index";
	private static final String dynamoUserScoreIndex = "user_id-score-index";
	private static final String dynamoGlobalDateIndex = "valid-date-index";
	private static final String dynamoGlobalScoreIndex = "valid-score-index";
	
	public final static PagingCache<RoundBean> topRounds = new PagingCache<RoundBean>(
			new AwsRoundPageLoader(
					AwsSupplier.getDynamo(), 
					SayPenisConfiguration.roundTable(), 
					Optional.of(dynamoGlobalScoreIndex)),
			Suppliers.memoizeWithExpiration(new Supplier<Integer>() {
					@Override
					public Integer get() {
						return SayPenisConfiguration.readCacheSize();
					}
				}, 10, TimeUnit.MINUTES));

}
