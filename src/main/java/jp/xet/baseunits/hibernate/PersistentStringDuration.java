/*
 * Copyright 2010-2018 Miyamoto Daisuke.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.xet.baseunits.hibernate;

import jp.xet.baseunits.time.Duration;
import jp.xet.baseunits.time.TimeUnit;

/**
 * {@link Duration}を文字列としてDBにデータを保存するHibernateユーザ型。
 * 
 * @author daisuke
 * @since 1.2
 */
@SuppressWarnings("serial")
public class PersistentStringDuration extends AbstractStringBasedBaseunitsType<Duration> {
	
	@Override
	public Class<Duration> returnedClass() {
		return Duration.class;
	}
	
	@Override
	protected Duration fromNonNullInternalType(String s) {
		String[] split = s.split(" ", 2);
		return new Duration(Long.parseLong(split[0]), TimeUnit.valueOf(split[1]));
	}
	
	@Override
	protected String toNonNullInternalType(Duration value) {
		return String.format("%s %s", value.breachEncapsulationOfQuantity(), value.breachEncapsulationOfUnit());
	}
}
