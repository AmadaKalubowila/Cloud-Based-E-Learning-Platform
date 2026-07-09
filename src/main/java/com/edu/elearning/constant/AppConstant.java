package com.edu.elearning.constant;

import org.springframework.data.jpa.repository.JpaRepository;

public final class AppConstant {
    public static final String DEFAULT_PAGE_No = "0";
    public static final String DEF_SORT_DIR = "asc";
    public static final String DEF_SORT_DIR_DESC = "desc";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SEAT_COUNT = "20";

    private AppConstant() {
    }

    public static <T, ID> int getDefaultPageSize(JpaRepository<T, ID> repository) {
        return (int) repository.count();
    }
}
