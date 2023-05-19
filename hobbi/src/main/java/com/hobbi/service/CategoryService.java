package com.hobbi.service;

import java.util.List;

import com.hobbi.model.entities.Category;
import com.hobbi.model.entities.enums.CategoryNameEnum;

public interface CategoryService {
    Category findByName(CategoryNameEnum category);

    List<Category> initCategories();
}

