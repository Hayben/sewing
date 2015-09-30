package com.sidooo.crawl.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.sidooo.saic.SaicInfo;
import com.sidooo.saic.SaicPublisher;

@Repository
public class CorprationRepository {
	@Autowired
	private MongoTemplate mongo;

	public Publicity getPublicity(String company) {
		Query query = new Query();
		Criteria criteria = Criteria.where("id").is(company);
		query.addCriteria(criteria);

		return mongo.findOne(query, Publicity.class);
	}

	public void save(String company, SaicPublisher publisher, SaicInfo info,
			String[] content) {

		Publicity publicity = getPublicity(company);

		Corpration corpration = null;
		if (publisher == SaicPublisher.BUREAU) {
			corpration = publicity.bureau;
		} else if (publisher == SaicPublisher.CORPRATION) {
			corpration = publicity.corpration;
		} else if (publisher == SaicPublisher.JUSTICE) {
			corpration = publicity.justice;
		} else if (publisher == SaicPublisher.OTHER) {
			corpration = publicity.other;
		} else {
			return;
		}

		List<String> list = Arrays.asList(content);
		if (info == SaicInfo.ABNORMITY) {
			corpration.abnormity.addAll(list);
		} else if (info == SaicInfo.ANNUAL) {
			corpration.annual.addAll(list);
		} else if (info == SaicInfo.MORTGAGE) {
			corpration.mortgage.addAll(list);
		} else if (info == SaicInfo.EQUITY) {
			corpration.equity.addAll(list);
		} else if (info == SaicInfo.FILING) {
			corpration.filing.addAll(list);
		} else if (info == SaicInfo.ILLEGAL) {
			corpration.illegal.addAll(list);
		} else if (info == SaicInfo.INSPECTION) {
			corpration.inspection.addAll(list);
		} else if (info == SaicInfo.INVESTOR) {
			corpration.investor.addAll(list);
		} else if (info == SaicInfo.PATENT) {
			corpration.patent.addAll(list);
		} else if (info == SaicInfo.PENALTY) {
			corpration.penalty.addAll(list);
		} else if (info == SaicInfo.PERMISSION) {
			corpration.permission.addAll(list);
		} else if (info == SaicInfo.REGISTRATION) {
			corpration.registration.addAll(list);
		} else {
			return;
		}
		
		mongo.save(publicity);
	}
}
