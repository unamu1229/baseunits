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
 * Copyright (c) 2004 Domain Language, Inc. (http://domainlanguage.com)
 * This free software is distributed under the "MIT" licence.
 * For more information, see http://timeandmoney.sourceforge.net.
 */
package jp.tricreo.baseunits.timeutil;

import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;

import jp.tricreo.baseunits.time.TimePoint;
import jp.tricreo.baseunits.time.TimeSource;
import jp.tricreo.baseunits.time.TimeSourceException;

import org.apache.commons.lang.Validate;

/**
 * NISTのdaytimeプロトコルクライアント。
 * 
 * <p>National Institute of Standards and Technology provides an Internet time server.</p>
 */
public class NISTClient {
	
	/** デフォルトの接続先ホスト名 */
	static final String DEFAULT_SERVER = "time.nist.gov";
	
	/** デフォルトの接続先ポート番号 */
	static final int DEFAULT_PORT = 13;
	
	/** 解析パターン文字列 */
	private static final String PATTERN = "y-M-d HH:mm:ss";
	
	/** socket通信バッファサイズ */
	private static final int BUFFER_SIZE = 256;
	
	private final String serverName;
	
	private final int port;
	

	/**
	 * インスタンスを生成する。
	 * 
	 * <p>時間の問い合わせ先は、{@code time.nist.gov:13}を使用する。</p>
	 */
	public NISTClient() {
		this(DEFAULT_SERVER, DEFAULT_PORT);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * <p>通常は使用しない。</p>
	 * 
	 * @param serverName サーバ名
	 * @param port ポート番号
	 */
	public NISTClient(String serverName, int port) {
		Validate.notNull(serverName);
		this.serverName = serverName;
		this.port = port;
	}
	
	/**
	 * ネットワーク時間に基づき現在の時刻を返す {@link TimeSource} を返す。
	 * 
	 * @return ネットワーク時間に基づき現在の時刻を返す {@link TimeSource}
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	public TimeSource timeSource() {
		return new TimeSource() {
			
			@Override
			public TimePoint now() {
				try {
					return NISTClient.this.now(serverName, port);
				} catch (IOException e) {
					throw new TimeSourceException("Problem obtaining network time: " + e.getMessage(), e);
				} catch (ParseException e) {
					throw new TimeSourceException("Problem obtaining network time: " + e.getMessage(), e);
				}
			}
		};
	}
	
	/**
	 * {@code time.nist.gov}が返す時間文字列を{@link TimePoint}型に変換する。
	 * 
	 * <p>例えば、{@code "55173 09-12-08 08:15:57 00 0 0 148.8 UTC(NIST) *"}等の文字列を
	 * 入力すると、{@code "Tue Dec 08 17:15:57 JST 2009"}を表す {@link TimePoint} を返す。
	 * 入力文字列の先頭6バイトおよび、24バイト目以降は無視する。</p>
	 * 
	 * @param nistRawFormattedString {@code time.nist.gov}が返す時間文字列
	 * @return 入力に基づく{@link TimePoint}
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 * @throws ParseException 引数nistRawFormattedStringの解析に失敗した場合
	 */
	protected TimePoint asTimePoint(String nistRawFormattedString) throws ParseException {
		Validate.notNull(nistRawFormattedString);
		String nistGist = nistRawFormattedString.substring(7, 24); // CHECKSTYLE IGNORE THIS LINE
		return TimePoint.parseGMTFrom(nistGist, PATTERN);
	}
	
	TimePoint now(String serverName, int port) throws IOException, ParseException {
		byte[] buffer = new byte[BUFFER_SIZE];
		Socket socket = new Socket(serverName, port);
		try {
			int length = socket.getInputStream().read(buffer);
			String nistTime = new String(buffer, 0, length);
			return asTimePoint(nistTime);
		} finally {
			socket.close();
		}
	}
}