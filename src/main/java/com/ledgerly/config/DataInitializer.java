package com.ledgerly.config;

import com.ledgerly.domain.Category;
import com.ledgerly.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    private static final List<String[]> DEFAULT_CATEGORIES = List.of(
            new String[]{"식비",      "EXPENSE"},
            new String[]{"교통비",    "EXPENSE"},
            new String[]{"의료/건강", "EXPENSE"},
            new String[]{"쇼핑",      "EXPENSE"},
            new String[]{"문화/여가", "EXPENSE"},
            new String[]{"주거/통신", "EXPENSE"},
            new String[]{"급여",      "INCOME"},
            new String[]{"부업/용돈", "INCOME"}
    );

    @Override
    public void run(ApplicationArguments args) {
        if (categoryRepository.existsByUserIsNull()) return;

        for (String[] entry : DEFAULT_CATEGORIES) {
            Category category = new Category();
            category.setName(entry[0]);
            category.setType(entry[1]);
            categoryRepository.save(category);
        }
    }
}
