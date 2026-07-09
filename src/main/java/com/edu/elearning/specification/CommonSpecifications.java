package com.edu.elearning.specification;


import com.edu.elearning.enums.Status;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;

public class CommonSpecifications {

    public static <T> Specification<T> HasStatus(Status status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static <T> Specification<T> getSpecification(
            Map<String, String> searchParams, Class<T> entityClass) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> andPredicates = new ArrayList<>();
            List<Predicate> orPredicates = new ArrayList<>();

            for (Map.Entry<String, String> entry : searchParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value == null || value.trim().isEmpty()) {
                    continue;
                }


                if (key.equalsIgnoreCase("or")) {
                    // Format: or=user.id:16,candidate:16
                    String[] conditions = value.split(",");
                    for (String cond : conditions) {
                        String[] parts = cond.split(":");
                        if (parts.length != 2) continue;

                        String fieldExpr = parts[0].trim();
                        String fieldValue = parts[1].trim();

                        if (fieldExpr.contains(".")) {
                            String[] relParts = fieldExpr.split("\\.");
                            String relation = relParts[0];
                            String field = relParts[1];

                            Join<T, ?> join = root.join(relation, JoinType.INNER);
                            Class<?> targetClass = join.getJavaType();

                            orPredicates.add(
                                    criteriaBuilder.equal(
                                            join.get(field), convertValue(targetClass, field, fieldValue)));
                        } else {
                            orPredicates.add(
                                    criteriaBuilder.equal(
                                            root.get(fieldExpr),
                                            convertValue(root.getModel().getBindableJavaType(), fieldExpr, fieldValue)));
                        }
                    }
                    continue; // skip default handling
                }

                // ---- Normal AND filters (existing code) ----
                Class<?> targetClass;
                if (key.contains(".")) {
                    String[] parts = key.split("\\.");
                    String relation = parts[0];
                    String field = parts[1];

                    Join<T, ?> join = root.join(relation, JoinType.INNER);
                    targetClass = join.getJavaType();

                    if (isStringField(targetClass, field)) {
                        andPredicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(join.get(field)), "%" + value.toLowerCase() + "%"));
                    } else {
                        andPredicates.add(
                                criteriaBuilder.equal(join.get(field), convertValue(targetClass, field, value)));
                    }

                } else {
                    targetClass = root.getModel().getBindableJavaType();

                    if (isStringField(targetClass, key)) {
                        andPredicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(root.get(key)), "%" + value.toLowerCase() + "%"));
                    } else {
                        andPredicates.add(
                                criteriaBuilder.equal(root.get(key), convertValue(targetClass, key, value)));
                    }
                }
            }

            Predicate finalPredicate = null;

            if (!andPredicates.isEmpty()) {
                finalPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));
            }

            if (!orPredicates.isEmpty()) {
                Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
                if (finalPredicate != null) {
                    finalPredicate = criteriaBuilder.and(finalPredicate, orPredicate);
                } else {
                    finalPredicate = orPredicate;
                }
            }

            return finalPredicate;
        };
    }

    private static Object convertValue(Class<?> entityClass, String fieldName, String value) {
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            Class<?> type = field.getType();

            if (type.equals(Long.class) || type.equals(long.class)) {
                return Long.parseLong(value);
            } else if (type.equals(Integer.class) || type.equals(int.class)) {
                return Integer.parseInt(value);
            } else if (type.equals(Double.class) || type.equals(double.class)) {
                return Double.parseDouble(value);
            } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                return Boolean.parseBoolean(value);
            } else {
                return value; // enums / Strings
            }
        } catch (Exception e) {
            return value;
        }
    }

    private static boolean isStringField(Class<?> entityClass, String fieldName) {
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            return field.getType().equals(String.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
