package com.privasia.mtos.common.search;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.domain.Specification;

public class SearchSpecification<T> implements Specification<T> {

	public static final Logger LOG = LogManager.getLogger(SearchSpecification.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SearchCriteria searchCriteria;

	public SearchSpecification(SearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		Predicate predicate = null;

		criteriaQuery.distinct(true);
		if (searchCriteria.getOperator().equals(">")) {
			if (searchCriteria.getValue() instanceof Date) {
				predicate = criteriaBuilder.greaterThanOrEqualTo(root.<Date> get(searchCriteria.getKey()), (Date) searchCriteria.getValue());
			} else {
				predicate = criteriaBuilder.greaterThanOrEqualTo(root.<String> get(searchCriteria.getKey()), searchCriteria.getValue().toString());
			}
		} else if (searchCriteria.getOperator().equals("<")) {
			if (searchCriteria.getValue() instanceof Date) {
				predicate = criteriaBuilder.lessThanOrEqualTo(root.<Date> get(searchCriteria.getKey()), (Date) searchCriteria.getValue());
			} else {
				predicate = criteriaBuilder.lessThanOrEqualTo(root.<String> get(searchCriteria.getKey()), searchCriteria.getValue().toString());
			}
		} else if (searchCriteria.getOperator().equals("=")) {
			Path<Object> fieldPath = null;
			String searchKey = searchCriteria.getKey();
			if (searchKey.contains(".")) {
				String[] fields = searchKey.split("\\.");
				for (String field : fields) {
					if (fieldPath == null) {
						fieldPath = root.get(field);
					} else {
						fieldPath = fieldPath.get(field);
					}
				}
			} else {
				fieldPath = root.get(searchKey);
			}

			if (fieldPath.getJavaType() == String.class) {
				if (searchCriteria.isSkipLike()) {
					predicate = criteriaBuilder.equal(criteriaBuilder.lower(getPath(String.class, root, searchKey)), String.valueOf(searchCriteria.getValue()).toLowerCase());
				} else {
					predicate = criteriaBuilder.like(criteriaBuilder.lower(getPath(String.class, root, searchKey)), "%" + String.valueOf(searchCriteria.getValue()).toLowerCase() + "%");
				}
			} else {
				predicate = criteriaBuilder.equal(fieldPath, searchCriteria.getValue());
			}
		} else if (searchCriteria.getOperator().equals("in")) {
			if (searchCriteria.getValue() instanceof List<?>) {
				predicate = getFieldPath(searchCriteria.getKey(), root).in(((List<?>) searchCriteria.getValue()).toArray());
			}
		} else if (searchCriteria.getOperator().equalsIgnoreCase(":")) {
			predicate = criteriaBuilder.equal(root.get(searchCriteria.getKey()), searchCriteria.getValue());
		} else if (searchCriteria.getOperator().equalsIgnoreCase("<>")) {
			predicate = criteriaBuilder.notEqual(root.get(searchCriteria.getKey()), searchCriteria.getValue());
		} else if (searchCriteria.getOperator().equalsIgnoreCase("isNull")) {
			predicate = criteriaBuilder.isNull(root.get(searchCriteria.getKey()));
		} else if (searchCriteria.getOperator().equalsIgnoreCase("isNotNull")) {
			predicate = criteriaBuilder.isNotNull(root.get(searchCriteria.getKey()));
		}
		return predicate;
	}

	private Path<Object> getFieldPath(String key, Root<T> root) {
		Path<Object> fieldPath = null;
		if (key.contains(".")) {
			String[] fields = key.split("\\.");
			for (String field : fields) {
				if (fieldPath == null) {
					fieldPath = root.get(field);
				} else {
					fieldPath = fieldPath.get(field);
				}
			}
		} else {
			fieldPath = root.get(key);
		}
		return fieldPath;
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	private <T, Object> Path<Object> getPath(Class<Object> clazz, Root<T> root, String key) {
		String[] fields = key.split("\\.");
		Path<?> path = root;
		for (String field : fields) {
			path = (Path<Object>) path.get(field);
		}
		return (Path<Object>) path;
	}

}
