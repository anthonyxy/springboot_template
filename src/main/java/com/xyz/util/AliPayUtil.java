package com.xyz.util;

import java.math.BigDecimal;

import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.xyz.config.AlipayConfig;

public class AliPayUtil {

	/**
	 * 支付宝创建app支付，返回订单信息给前端
	 * 
	 * @param body
	 *            订单描述
	 * @param outTradeNo
	 *            唯一订单号
	 * @param totalAmount
	 *            总金额
	 * 
	 * @return AlipayTradeAppPayResponse
	 *         isSuccess()判断是否成功/getBody()获取orderString可以直接给客户端请求
	 * @throws Exception
	 */
	public static AlipayTradeAppPayResponse appPayCreate(String body, String outTradeNo, BigDecimal totalAmount)
			throws Exception {
		// 实例化具体API对应的request类，类名称和接口名称对应，当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

		// SDK已经封装掉了公共参数，这里只需要传入业务参数
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setTimeoutExpress(AlipayConfig.TIME_OUT); // 过期时间
		model.setSubject(AlipayConfig.SUBJECT_PREFIX + body); // 交易标题
		model.setOutTradeNo(outTradeNo); // 唯一订单号
		model.setTotalAmount(totalAmount.toString()); // 总金额

		request.setBizModel(model); // 加入服务端请求参数
		request.setNotifyUrl(AlipayConfig.NOTIFY_URL); // 通知服务端地址

		// 这里和普通的接口调用不同，使用的是sdkExecute
		// response.getBody()就是orderString，可以直接给客户端请求，无需再做处理
		AlipayTradeAppPayResponse response = AlipayConfig.alipayClient.sdkExecute(request);

		return response;

	}

	/**
	 * 支付宝按交易号退款
	 *
	 * @param tradeNo
	 *            需要退款的支付宝交易号
	 * @param refundAmount
	 *            需要退款的金额
	 *
	 * @return AlipayTradeRefundResponse
	 * @throws Exception
	 */
	public static AlipayTradeRefundResponse appPayRefund(String tradeNo, BigDecimal refundAmount) throws Exception {

		AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();

		AlipayTradeRefundModel model = new AlipayTradeRefundModel();
		model.setTradeNo(tradeNo);
		model.setRefundAmount(refundAmount.toString());

		request.setBizModel(model);

		AlipayTradeRefundResponse response = AlipayConfig.alipayClient.execute(request);

		return response;

	}

	/**
	 * 支付宝按交易号或订单号查询状态
	 * 
	 * @param orderNumber
	 *            商户订单号（可选）
	 * @param aliNumber
	 *            支付宝交易号（可选）
	 * 
	 * @return AlipayTradeRefundResponse
	 * @throws Exception
	 */
	public static AlipayTradeQueryResponse getPayState(String orderNumber, String aliNumber) throws Exception {

		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

		AlipayTradeQueryModel model = new AlipayTradeQueryModel();
		model.setOutTradeNo(orderNumber);
		model.setTradeNo(aliNumber);

		request.setBizModel(model);

		AlipayTradeQueryResponse response = AlipayConfig.alipayClient.execute(request);

		return response;

	}

}
