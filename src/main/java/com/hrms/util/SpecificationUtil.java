package com.hrms.util;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for building JPA Specifications
 */
public class SpecificationUtil {

    /**
     * Create a Specification that combines multiple predicates with AND
     */
    public static <T> Specification<T> combineWithAnd(List<Specification<T>> specifications) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Specification<T> spec : specifications) {
                if (spec != null) {
                    Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Create a Specification for LIKE search (case insensitive)
     */
    public static <T> Specification<T> like(String field, String value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null || value.isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get(field)),
                "%" + value.toLowerCase() + "%"
            );
        };
    }

    /**
     * Create a Specification for equality
     */
    public static <T> Specification<T> equals(String field, Object value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(field), value);
        };
    }

    /**
     * Create a Specification for NOT DELETED (soft delete)
     */
    public static <T> Specification<T> notDeleted() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("deleted"), false);
    }
}
