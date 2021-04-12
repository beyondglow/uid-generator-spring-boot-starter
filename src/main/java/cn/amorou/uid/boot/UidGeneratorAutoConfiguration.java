/*
 * Copyright 2020 DuJiang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.amorou.uid.boot;

import static cn.amorou.uid.boot.UidGeneratorProperties.UID_PREFIX;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import org.apache.commons.lang.ArrayUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import cn.amorou.uid.UidGenerator;
import cn.amorou.uid.buffer.RejectedPutBufferHandler;
import cn.amorou.uid.buffer.RejectedTakeBufferHandler;
import cn.amorou.uid.impl.CachedUidGenerator;
import cn.amorou.uid.impl.DefaultUidGenerator;
import cn.amorou.uid.worker.DisposableWorkerIdAssigner;
import cn.amorou.uid.worker.SimpleWorkerIdAssigner;
import cn.amorou.uid.worker.WorkerIdAssigner;

/**
 * UidGeneratorAutoConfiguration
 * 
 * @author DuJiang
 */
@Configuration
@EnableConfigurationProperties(UidGeneratorProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@ConditionalOnProperty(prefix = UID_PREFIX, name = "auto", havingValue = "true", matchIfMissing = true)
public class UidGeneratorAutoConfiguration {

	private static final String DISPOSABLE_WORKER_ID_ASSIGNER = "cn.amorou.uid.worker.DisposableWorkerIdAssigner";
	private static final String SIMPLE_WORKER_ID_ASSIGNER = "cn.amorou.uid.worker.SimpleWorkerIdAssigner";

	private static final String DEFAULT_UID_GENERATOR = "cn.amorou.uid.impl.DefaultUidGenerator";
	private static final String CACHED_UID_GENERATOR = "cn.amorou.uid.impl.CachedUidGenerator";

	private static final String WORKER_ID_ASSIGNER_IMPL = "worker-id-assigner-impl";
	private static final String UID_GENERATOR_IMPL = "uid-generator-impl";

	@Resource
	private ApplicationContext applicationContext;

	@Bean(name = UidGeneratorProperties.UID_SQL_SESSION_FACTORY_NAME)
	@ConditionalOnBean(DataSource.class)
	public SqlSessionFactory uidSqlSessionFactory(@Qualifier("uidedDataSource") DataSource dataSource, MybatisPlusProperties mybatisPlusProperties) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		// 解决 application.properties 中 配置mybatis.mapper-locations 失效的问题
		bean.setMapperLocations(
				(org.springframework.core.io.Resource[])ArrayUtils.addAll(
						mybatisPlusProperties.resolveMapperLocations(),
				new PathMatchingResourcePatternResolver().getResources(UidGeneratorProperties.MYBATIS_MAPPER_LOCATIONS)));
		return bean.getObject();
	}

	@Bean
	@ConditionalOnBean(SqlSessionFactory.class)
	@ConditionalOnProperty(prefix = UID_PREFIX, name = WORKER_ID_ASSIGNER_IMPL, havingValue = DISPOSABLE_WORKER_ID_ASSIGNER, matchIfMissing = false)
	public WorkerIdAssigner disposableWorkerIdAssigner() {
		return new DisposableWorkerIdAssigner();
	}

	@Bean
	@ConditionalOnProperty(prefix = UID_PREFIX, name = WORKER_ID_ASSIGNER_IMPL, havingValue = SIMPLE_WORKER_ID_ASSIGNER, matchIfMissing = true)
	public WorkerIdAssigner simpleWorkerIdAssigner() {
		return new SimpleWorkerIdAssigner();
	}

	@Bean
	@ConditionalOnMissingBean(UidGenerator.class)
	@ConditionalOnProperty(prefix = UID_PREFIX, name = UID_GENERATOR_IMPL, havingValue = DEFAULT_UID_GENERATOR, matchIfMissing = true)
	public UidGenerator defaultUidGenerator(WorkerIdAssigner workerIdAssigner, UidGeneratorProperties uidGeneratorProperties) {
		DefaultUidGenerator uidGenerator = new DefaultUidGenerator();
		uidGenerator.setWorkerIdAssigner(workerIdAssigner);
		uidGenerator.setEpochStr(uidGeneratorProperties.getEpochStr());
		uidGenerator.setSeqBits(uidGeneratorProperties.getSeqBits());
		uidGenerator.setTimeBits(uidGeneratorProperties.getTimeBits());
		uidGenerator.setWorkerBits(uidGeneratorProperties.getWorkerBits());
		return uidGenerator;
	}

	@Bean
	@ConditionalOnBean(SqlSessionFactory.class)
	@ConditionalOnMissingBean(UidGenerator.class)
	@ConditionalOnProperty(prefix = UID_PREFIX, name = UID_GENERATOR_IMPL, havingValue = CACHED_UID_GENERATOR, matchIfMissing = false)
	public UidGenerator cachedUidGenerator(WorkerIdAssigner workerIdAssigner, UidGeneratorProperties uidGeneratorProperties, RejectedPutBufferHandler rejectedPutBufferHandler, RejectedTakeBufferHandler rejectedTakeBufferHandler) {
		CachedUidGenerator uidGenerator = new CachedUidGenerator();
		uidGenerator.setWorkerIdAssigner(workerIdAssigner);
		if (uidGeneratorProperties.getBoostPower() > 0) {
			uidGenerator.setBoostPower(uidGeneratorProperties.getBoostPower());
		}
		if (uidGeneratorProperties.getScheduleInterval() > 0) {
			uidGenerator.setScheduleInterval(uidGeneratorProperties.getScheduleInterval());
		}
		uidGenerator.setSeqBits(uidGeneratorProperties.getSeqBits());
		uidGenerator.setTimeBits(uidGeneratorProperties.getTimeBits());
		uidGenerator.setWorkerBits(uidGeneratorProperties.getWorkerBits());

		if (rejectedPutBufferHandler != null) {
			uidGenerator.setRejectedPutBufferHandler(rejectedPutBufferHandler);
		}
		if (rejectedTakeBufferHandler != null) {
			uidGenerator.setRejectedTakeBufferHandler(rejectedTakeBufferHandler);
		}
		return uidGenerator;
	}
}