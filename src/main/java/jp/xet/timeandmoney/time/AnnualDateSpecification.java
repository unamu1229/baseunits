/*
 * Copyright 2010 Daisuke Miyamoto.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 * ----
 * Copyright (c) 2004 Domain Language, Inc. (http://domainlanguage.com) This
 * free software is distributed under the "MIT" licence. See file licence.txt.
 * For more information, see http://timeandmoney.sourceforge.net.
 */
package jp.xet.timeandmoney.time;

import java.util.Iterator;
import java.util.NoSuchElementException;

import jp.xet.timeandmoney.util.ImmutableIterator;

import org.apache.commons.lang.Validate;

/**
 * 1年間に1度だけ仕様を満たす日付仕様。
 */
public abstract class AnnualDateSpecification extends DateSpecification {
	
	@Override
	public CalendarDate firstOccurrenceIn(CalendarInterval interval) {
		Validate.notNull(interval);
		CalendarDate firstTry = ofYear(interval.start().getCalendarMonth().breachEncapsulationOfYear());
		if (interval.includes(firstTry)) {
			return firstTry;
		}
		CalendarDate secondTry = ofYear(interval.start().getCalendarMonth().breachEncapsulationOfYear() + 1);
		if (interval.includes(secondTry)) {
			return secondTry;
		}
		return null;
	}
	
	@Override
	public Iterator<CalendarDate> iterateOver(final CalendarInterval interval) {
		Validate.notNull(interval);
		return new ImmutableIterator<CalendarDate>() {
			
			CalendarDate next = firstOccurrenceIn(interval);
			
			int year = next.getCalendarMonth().breachEncapsulationOfYear();
			

			@Override
			public boolean hasNext() {
				return next != null;
			}
			
			@Override
			public CalendarDate next() {
				if (hasNext() == false) {
					throw new NoSuchElementException();
				}
				CalendarDate current = next;
				year += 1;
				next = AnnualDateSpecification.this.ofYear(year);
				if (interval.includes(next) == false) {
					next = null;
				}
				return current;
			}
		};
	}
	
	/**
	 * 指定した年においてこの日付仕様を満たす年月日を返す。
	 * 
	 * @param year 西暦年をあらわす数
	 * @return {@link CalendarDate}
	 */
	public abstract CalendarDate ofYear(int year);
}