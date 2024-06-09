package com.guroomsoft.icms.biz.marketprice.crawler;

import com.guroomsoft.icms.biz.marketprice.dto.NonferrousPrice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 한국 비철금속협회
 * LME 시스정보 조회
 * 현물 US$ / 톤
 * CU 동 / AL 알루미늄 / ZN 아연 / Sn 주석  / Ni 니켈 / Pb 납
 */
@Slf4j
@Component
public class NonferrousCrawler {
	/**
	 * 비철금속협회 LME 시세 스크래핑
	 *  2024. 03. 08
	 *  <th>Cu</th> 8,552.5
	 *  <th>Al</th> 2,209.0
	 *  <th>Zn</th> 2,501.5
	 *  <th>Pb</th> 2,125.0
	 *  <th>Ni</th> 17,845.0
	 *  <th>Sn</th> 27,485.0
	 */
	public List<NonferrousPrice> scrapeMarketPrice() throws Exception
	{
		String pageUrl = "https://www.nonferrous.or.kr/stats/?act=sub3";

		try {
			Document doc = Jsoup.connect(pageUrl).get();

			Elements contents = doc.select("#content > div.sub_page > div.tableStyle01 > table > tbody > tr");

			List<NonferrousPrice> rsltList = new ArrayList<NonferrousPrice>();
			for (int i = 0; i < contents.size(); i++)
			{
				log.debug("{}", i);

				Element element = contents.get(i);
				Elements tdElements = element.getElementsByTag("td");
 				if (tdElements == null || tdElements.size() == 0) {
					 continue;
				}

				NonferrousPrice data = new NonferrousPrice();
				data.setMnDate(StringUtils.replace(StringUtils.defaultString(tdElements.get(0).text()), ". ", "")); // 일자  2024. 03. 08
				data.setCuPrc(BigDecimal.valueOf(Double.valueOf(StringUtils.replace(StringUtils.defaultString(tdElements.get(1).text(), "0"), ",", ""))));
				data.setAlPrc(BigDecimal.valueOf(Double.valueOf(StringUtils.replace(StringUtils.defaultString(tdElements.get(2).text(), "0"), ",", ""))));
				data.setZnPrc(BigDecimal.valueOf(Double.valueOf(StringUtils.replace(StringUtils.defaultString(tdElements.get(3).text(), "0"), ",", ""))));
				data.setPbPrc(BigDecimal.valueOf(Double.valueOf(StringUtils.replace(StringUtils.defaultString(tdElements.get(4).text(), "0"), ",", ""))));
				data.setNiPrc(BigDecimal.valueOf(Double.valueOf(StringUtils.replace(StringUtils.defaultString(tdElements.get(5).text(), "0"), ",", ""))));
				data.setSnPrc(BigDecimal.valueOf(Double.valueOf(StringUtils.replace(StringUtils.defaultString(tdElements.get(6).text(), "0"), ",", ""))));

				rsltList.add(data);
			}

			return rsltList;

		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return null;
	}
}
