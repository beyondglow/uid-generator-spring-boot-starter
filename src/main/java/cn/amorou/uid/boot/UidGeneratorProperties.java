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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * UidGeneratorProperties
 * 
 * @author DuJiang
 */
@ConfigurationProperties(prefix = UidGeneratorProperties.UID_PREFIX)
public class UidGeneratorProperties {

	public static final String UID_PREFIX = "uid";

	public static final String MAPPER_BASE_PACKAGE = "cn.amorou.uid.worker.dao";
	public static final String MYBATIS_MAPPER_LOCATIONS = "classpath*:/META-INF/mybatis/mapper/WORKER_NODE.xml";

	public static final String UID_SQL_SESSION_FACTORY_NAME = "uidSqlSessionFactory";

	private int boostPower;

	private String epochStr;

	private long scheduleInterval;

	private int seqBits;

	private int timeBits;

	private int workerBits;

	public int getBoostPower() {
		return boostPower;
	}

	public void setBoostPower(int boostPower) {
		this.boostPower = boostPower;
	}

	public String getEpochStr() {
		return epochStr;
	}

	public void setEpochStr(String epochStr) {
		this.epochStr = epochStr;
	}

	public long getScheduleInterval() {
		return scheduleInterval;
	}

	public void setScheduleInterval(int scheduleInterval) {
		this.scheduleInterval = scheduleInterval;
	}

	public int getSeqBits() {
		return seqBits;
	}

	public void setSeqBits(int seqBits) {
		this.seqBits = seqBits;
	}

	public int getTimeBits() {
		return timeBits;
	}

	public void setTimeBits(int timeBits) {
		this.timeBits = timeBits;
	}

	public int getWorkerBits() {
		return workerBits;
	}

	public void setWorkerBits(int workerBits) {
		this.workerBits = workerBits;
	}

}
