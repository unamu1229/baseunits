/*
 * Copyright 2010 TRICREO, Inc. (http://tricreo.jp/)
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
 * Copyright (c) 2005 Domain Language, Inc. (http://domainlanguage.com) This
 * free software is distributed under the "MIT" licence.
 * For more information, see http://timeandmoney.sourceforge.net.
 */
package jp.tricreo.baseunits.time;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.Validate;

/**
 * 単位時間あたりの何らかの量（時間に対する割合）をあらわすクラス。
 * 
 * <p>例えば、時給・時速など。</p>
 */
public class TimeRate {
	
	/** 単位時間あたりの量 */
	final BigDecimal quantity;
	
	/** 単位時間 */
	final Duration unit;
	

	/**
	 * インスタンスを生成する。
	 * 
	 * @param quantity 単位時間あたりの量
	 * @param unit 単位時間
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	public TimeRate(BigDecimal quantity, Duration unit) {
		Validate.notNull(quantity);
		Validate.notNull(unit);
		this.quantity = quantity;
		this.unit = unit;
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param quantity 単位時間あたりの量
	 * @param unit 単位時間
	 * @throws IllegalArgumentException 引数unitに{@code null}を与えた場合
	 * @throws NumberFormatException see {@link BigDecimal#BigDecimal(double)}
	 */
	public TimeRate(double quantity, Duration unit) {
		this(new BigDecimal(quantity), unit);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param quantity 単位時間あたりの量をあらわす文字列
	 * @param unit 単位時間
	 * @throws NullPointerException 引数quantityに{@code null}を与えた場合
	 * @throws IllegalArgumentException 引数unitに{@code null}を与えた場合
	 * @throws NumberFormatException see {@link BigDecimal#BigDecimal(String)}
	 */
	public TimeRate(String quantity, Duration unit) {
		this(new BigDecimal(quantity), unit);
	}
	
	/**
	 * このオブジェクトの{@link #quantity}フィールド（単位時間あたりの量）を返す。
	 * 
	 * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
	 * 
	 * @return 単位時間あたりの量
	 */
	public BigDecimal breachEncapsulationOfQuantity() {
		return quantity;
	}
	
	/**
	 * このオブジェクトの{@link #unit}フィールド（単位時間）を返す。
	 * 
	 * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
	 * 
	 * @return 単位時間
	 */
	public Duration breachEncapsulationOfUnit() {
		return unit;
	}
	
	@Override
	public boolean equals(Object obj) {
		assert quantity != null;
		assert unit != null;
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TimeRate other = (TimeRate) obj;
		if (quantity.equals(other.quantity) == false) {
			return false;
		}
		if (unit.equals(other.unit) == false) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + quantity.hashCode();
		result = prime * result + unit.hashCode();
		return result;
	}
	
	/**
	 * 指定した時間量にこの時間割合を適用した場合の絶対量を取得する。
	 * 
	 * <p>レート計算における数字の丸めは行わない。</p>
	 * 
	 * <p>例えば、3時間に対して時給1000円を適用すると、3000円となる。</p>
	 * 
	 * @param duration 時間量
	 * @return 絶対量
	 * @throws IllegalArgumentException 引数durationの単位を、このオブジェクトの単位時間の単位に変換できない場合
	 * @throws ArithmeticException 引数{@code duration}の時間量が単位時間で割り切れない場合
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	public BigDecimal over(Duration duration) {
		Validate.notNull(duration);
		return over(duration, RoundingMode.UNNECESSARY);
	}
	
	/**
	 * 指定した時間量にこの時間割合を適用した場合の絶対量を取得する。
	 * 
	 * @param duration 時間量
	 * @param scale スケール
	 * @param roundMode 丸めモード
	 * @return 絶対量
	 * @throws IllegalArgumentException 引数durationの単位を、このオブジェクトの単位時間の単位に変換できない場合
	 * @throws ArithmeticException 引数 {@code roundMode} に {@link jp.tricreo.baseunits.util.Rounding#UNNECESSARY} を
	 * 			指定したにもかかわらず、引数{@code duration}の時間量が単位時間で割り切れない場合
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 * @deprecated 次期メジャーバージョンアップ（v2.0）以降、この機能はサポートされません。
	* 			use {@link #over(Duration, int, RoundingMode)}
	 */
	@Deprecated
	public BigDecimal over(Duration duration, int scale, jp.tricreo.baseunits.util.Rounding roundMode) {
		Validate.notNull(duration);
		Validate.notNull(roundMode);
		return duration.dividedBy(unit).times(quantity).decimalValue(scale, roundMode);
	}
	
	/**
	 * 指定した時間量にこの時間割合を適用した場合の絶対量を取得する。
	 * 
	 * @param duration 時間量
	 * @param scale スケール
	 * @param roundingMode 丸めモード
	 * @return 絶対量
	 * @throws IllegalArgumentException 引数durationの単位を、このオブジェクトの単位時間の単位に変換できない場合
	 * @throws ArithmeticException 引数 {@code roundingMode} に {@link RoundingMode#UNNECESSARY} を指定したにもかかわらず、
	 * 			引数{@code duration}の時間量が単位時間で割り切れない場合
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	public BigDecimal over(Duration duration, int scale, RoundingMode roundingMode) {
		Validate.notNull(duration);
		Validate.notNull(roundingMode);
		return duration.dividedBy(unit).times(quantity).decimalValue(scale, roundingMode);
	}
	
	/**
	 * 指定した時間量にこの時間割合を適用した場合の絶対量を取得する。
	 * 
	 * @param duration 時間量
	 * @param roundingMode 丸めモード
	 * @return 絶対量
	 * @throws IllegalArgumentException 引数durationの単位を、このオブジェクトの単位時間の単位に変換できない場合
	 * @throws ArithmeticException 引数 {@code roundingMode} に {@link jp.tricreo.baseunits.util.Rounding#UNNECESSARY} を
	 * 			指定したにもかかわらず、引数{@code duration}の時間量が単位時間で割り切れない場合
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 * @deprecated 次期メジャーバージョンアップ（v2.0）以降、この機能はサポートされません。
	* 			use {@link #over(Duration, RoundingMode)}
	 */
	@Deprecated
	public BigDecimal over(Duration duration, jp.tricreo.baseunits.util.Rounding roundingMode) {
		Validate.notNull(duration);
		Validate.notNull(roundingMode);
		return over(duration, scale(), roundingMode);
	}
	
	/**
	 * 指定した時間量にこの時間割合を適用した場合の絶対量を取得する。
	 * 
	 * @param duration 時間量
	 * @param roundingMode 丸めモード
	 * @return 絶対量
	 * @throws IllegalArgumentException 引数durationの単位を、このオブジェクトの単位時間の単位に変換できない場合
	 * @throws ArithmeticException 引数 {@code roundingMode} に {@link RoundingMode#UNNECESSARY} を指定したにもかかわらず、
	 * 			引数{@code duration}の時間量が単位時間で割り切れない場合
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	public BigDecimal over(Duration duration, RoundingMode roundingMode) {
		Validate.notNull(duration);
		Validate.notNull(roundingMode);
		return over(duration, scale(), roundingMode);
	}
	
	/**
	 * スケールを取得する。
	 * 
	 * @return スケール
	 */
	public int scale() {
		return quantity.scale();
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(quantity);
		buffer.append(" per ");
		buffer.append(unit);
		return buffer.toString();
	}
}