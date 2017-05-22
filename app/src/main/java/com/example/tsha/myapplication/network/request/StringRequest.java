/**
 * 
 */
package com.example.tsha.myapplication.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.tsha.myapplication.network.NetworkManager;


/**
 * <pre>
 *  1. 기능 : 문자열 요청 클래스
 *  2. 처리 개요 : 
 *     - 문자열 요청 클래스
 *     - Volley Library 사용
 *  3. 주의사항 : 
 *  4. 작성자/작성일 : 이동식 / 2014. 11. 11.
 * ===================================
 *  5. 수정사항
 *  5.1 요구사항 ID :
 *     - 수정자/수정일 : 
 *     - 수정사유/내역 :
 * ===================================
 * </pre>
 *  @author : 이동식
 *  @version : v1.0.0
 *  @see :  참조
 *  @since : J2SE 6.0
 **/
public class StringRequest extends Request<byte[]> {

	/** 성공 리스너 */
	private NetworkManager.StringSuccessListener successListener;
	/** 에러 리스너 */
	private NetworkManager.ErrorListener errorListener;

	/**
	 * <pre>
	 * 1. 기능 : StringRequest 생성자
	 * 2. 처리개요 : 
	 *     - StringRequest 생성자로 초기화를 수행한다.
	 * 3. 주의사항 : 
	 * ===================================
	 * 4. 작성자/작성일 : 이동식 / 2014. 11. 11.
	 * ===================================
	 * 5. 수정사항
	 * 5.1 요구사항 ID :
	 *     - 수정자/수정일 :
	 *     - 수정사유/내역 :
	 * ===================================
	 * </pre>
	 * @param requestType 요청 타입
	 * @param url 요청 url
	 * @param successListener 성공 리스너
	 * @param listener 에러 리스너
	 */
	public StringRequest(int requestType, String url, NetworkManager.StringSuccessListener successListener, NetworkManager.ErrorListener listener) {
		super(requestType, url, listener);
		this.successListener = successListener;
		this.errorListener = listener;
	}

	@Override
	protected Response<byte[]> parseNetworkResponse(NetworkResponse paramNetworkResponse) {
		return Response.success(paramNetworkResponse.data, HttpHeaderParser.parseCacheHeaders(paramNetworkResponse));

	}

	@Override
	protected void deliverResponse(byte[] arg0) {
		successListener.onResponse(new String(arg0));
		successListener.clear();
		errorListener.clear();
	}
}
