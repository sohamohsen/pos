package com.pos.user.repository;

import com.pos.user.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterUsers(
            Boolean isActive,
            String code,
            String name,
            String username,
            String role,
            LocalDate start,
            LocalDate end
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            if (code != null && !code.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("code")),
                        "%" + code.toLowerCase() + "%"
                ));
            }

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                ));
            }

            if (username != null && !username.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("username")),
                        "%" + username.toLowerCase() + "%"
                ));
            }

            if (role != null && !role.isBlank()) {
                predicates.add(cb.equal(root.get("role"), role));
            }

            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        start.atStartOfDay()
                ));
            }

            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdAt"),
                        end.atTime(23, 59, 59)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
