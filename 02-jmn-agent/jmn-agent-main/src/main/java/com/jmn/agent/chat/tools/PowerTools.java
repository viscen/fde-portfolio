package com.jmn.agent.chat.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 电力营销领域工具集（模拟实现，演示 Function Calling）
 *
 * <p>真实场景下这些方法内部会调用营销系统/负荷管理系统接口，这里用确定性数据模拟，
 * 以便离线演示与单元验证。</p>
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@Component
@Slf4j
public class PowerTools {

	/** 工具清单（供 /api/chat/tools 展示） */
	public static final List<Map<String, String>> TOOL_SUMMARIES = List.of(
			Map.of("name", "queryPowerLoad", "description", "查询指定地区指定日期的电网负荷数据"),
			Map.of("name", "calcPowerBill", "description", "按阶梯电价试算电费"),
			Map.of("name", "queryOutagePlan", "description", "查询指定区域的计划停电安排")
	);

	/** 居民阶梯电价（元/kWh）：一档0-260、二档261-600、三档600以上 */
	private static final double[] RESIDENT_PRICE = {0.56, 0.61, 0.86};
	/** 工商业统一电价（元/kWh） */
	private static final double BUSINESS_PRICE = 0.72;

	@Tool(description = "查询指定地区指定日期的电网负荷数据（单位：万千瓦），date 不填默认为今天")
	public String queryPowerLoad(
			@ToolParam(description = "地区名，例如：深圳") String region,
			@ToolParam(required = false, description = "日期，格式 yyyy-MM-dd") String date) {
		log.info("工具调用 queryPowerLoad region={} date={}", region, date);
		// 确定性模拟：由地区名生成稳定的伪随机负荷曲线
		int seed = Math.abs((region + (date == null ? "today" : date)).hashCode());
		double peak = 800 + (seed % 400);
		String curve = "%d时:%.0f, %d时:%.0f, %d时:%.0f(晚高峰), %d时:%.0f".formatted(
				8, peak * 0.7, 12, peak * 0.85, 19, peak, 23, peak * 0.5);
		return "地区:%s, 日期:%s, 最高负荷:%.0f万千瓦, 负荷曲线[%s]".formatted(region, date == null ? "今天" : date, peak, curve);
	}

	@Tool(description = "按阶梯电价试算月度电费（元）")
	public String calcPowerBill(
			@ToolParam(description = "月用电量，单位千瓦时") double usageKwh,
			@ToolParam(description = "客户类型：居民 或 工商业") String customerType) {
		log.info("工具调用 calcPowerBill usageKwh={} customerType={}", usageKwh, customerType);
		if (usageKwh < 0) {
			return "用电量不能为负数";
		}
		double bill;
		if (customerType != null && customerType.contains("工商")) {
			bill = usageKwh * BUSINESS_PRICE;
		} else {
			double remain = usageKwh;
			bill = 0;
			for (double price : RESIDENT_PRICE) {
				double tier = Math.min(remain, price == RESIDENT_PRICE[0] ? 260 : (price == RESIDENT_PRICE[1] ? 340 : remain));
				bill += tier * price;
				remain -= tier;
				if (remain <= 0) {
					break;
				}
			}
		}
		return "客户类型:%s, 月用电量:%.2f千瓦时, 试算电费:%.2f元".formatted(
				customerType == null ? "居民" : customerType, usageKwh, bill);
	}

	@Tool(description = "查询指定区域未来一周的计划停电安排")
	public String queryOutagePlan(@ToolParam(description = "区域名，例如：南山区") String region) {
		log.info("工具调用 queryOutagePlan region={}", region);
		int seed = Math.abs(region.hashCode()) % 3;
		return switch (seed) {
			case 0 -> region + "：本周无计划停电";
			case 1 -> region + "：周四 09:00-12:00 计划检修停电（涉及3个台区，已提前48小时通知）";
			default -> region + "：周六 14:00-17:00 计划检修停电（涉及1个台区），另周三 02:00-04:00 临时旁路作业不停电";
		};
	}
}
