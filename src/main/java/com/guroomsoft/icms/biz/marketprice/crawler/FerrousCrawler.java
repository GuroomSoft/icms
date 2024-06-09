package com.guroomsoft.icms.biz.marketprice.crawler;

import com.guroomsoft.icms.biz.marketprice.dto.FerrousPrice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 한국수입협회 국제 원자재가격 정보
 * 철강재
 */
@Slf4j
@Component
public class FerrousCrawler {
	/**
	 * 한국수입협회 국제 원자재가격 정보
	 * 품목 / 단위 / 거래시장(조건) / 현물/선물  / 가격 / 전주평균 / 전월평균 / 전일비 / 전주비 / 전월비
	 */
	public Map<String, Object> scrapeMarketPrice() throws Exception
	{
		String pageUrl = "https://www.koimaindex.com/koimaindex/koima/price/retrieveList.do";
		Map<String, String> postData = new HashMap<>();
		postData.put("priceSearchVO.mainItemNo", "80");
		postData.put("priceSearchVO.searchMainItemNo", "80");

		try {
			Document doc = Jsoup.connect(pageUrl)
					.data(postData)
					.post();

			Elements dateEl = doc.select("#listForm > div.mCount1");
			log.debug("{}", dateEl.get(0).text());
			String baseDate = StringUtils.defaultString(dateEl.get(0).text(), " ");
			baseDate = baseDate.replaceAll("[^0-9]", "");
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("baseDate", baseDate);
			List<FerrousPrice> dataList = new ArrayList<FerrousPrice>();
			Elements rowEl = doc.select("#listForm > div.mBox1.type2 > div.mBoard1.lineLt > table > tbody > tr");
			for (int i = 0; i < rowEl.size(); i++) {
//				log.info("{}", rowEl.get(i).text());
				Element element = rowEl.get(i);
				Elements tdElements = element.getElementsByTag("td");
				if (tdElements == null || tdElements.size() == 0) {
					continue;
				}

				FerrousPrice data = new FerrousPrice();
				data.setMnDate(baseDate);
				data.setItem(StringUtils.defaultString(tdElements.get(0).text(), "0"));
				data.setUnit(StringUtils.defaultString(tdElements.get(1).text(), "-"));
				data.setMarket(StringUtils.defaultString(tdElements.get(2).text(), "-"));
				data.setFutures(StringUtils.defaultString(tdElements.get(3).text(), "-"));
				data.setPrice(StringUtils.defaultString(tdElements.get(4).text(), "0"));
				data.setPrevWeekAvg(StringUtils.defaultString(tdElements.get(5).text(), "0"));
				data.setPrevMonthAvg(StringUtils.defaultString(tdElements.get(6).text(), "0"));
				List<String> prevDate = getBracketText(StringUtils.defaultString(tdElements.get(7).text(), "-"));
				data.setPrevDateAmt(prevDate.get(0));
				data.setPrevDateRate(prevDate.get(1));
				List<String> prevWeek = getBracketText(StringUtils.defaultString(tdElements.get(8).text(), "-"));
				data.setPrevWeekAmt(prevWeek.get(0));
				data.setPrevWeekRate(prevWeek.get(1));
				List<String> prevMonth = getBracketText(StringUtils.defaultString(tdElements.get(9).text(), "-"));
				data.setPrevMonthAmt(prevMonth.get(0));
				data.setPrevMonthRate(prevMonth.get(1));
				dataList.add(data);
			}
			resultMap.put("data", dataList);
			return resultMap;
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return null;
	}

	public List<String> getBracketText(String text)
	{
		ArrayList<String> bracketTextList = new ArrayList<String>();

		String part1Text = new String();
		String part2Text = new String();

		int startIndex = text.indexOf("(");
		int endIndex = text.indexOf(")");

		part1Text = text.substring(0, startIndex);
		part2Text = text.substring(startIndex+1, endIndex);

		/** 추출된 괄호 데이터를 삽입 **/
		bracketTextList.add(part1Text.trim());
		bracketTextList.add(part2Text.trim());

		return bracketTextList;
	}

}
