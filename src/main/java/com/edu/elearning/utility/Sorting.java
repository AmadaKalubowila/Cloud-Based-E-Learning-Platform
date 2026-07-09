package com.edu.elearning.utility;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Sorting {

    public static Pageable sorting(int page, int size, String sortField, String sortDirection) {
        Sort sort =
                sortDirection.equalsIgnoreCase("ASC")
                        ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending();

        return PageRequest.of(page, size, sort);
    }
}

