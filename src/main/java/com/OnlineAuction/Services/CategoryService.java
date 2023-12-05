package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.CategoryDTO;
import com.OnlineAuction.Exceptions.UnableToDeleteException;
import com.OnlineAuction.Models.Category;
import com.OnlineAuction.Models.Lot;
import com.OnlineAuction.Repositories.CategoriesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoriesRepository categoriesRepository;

    @Autowired
    public CategoryService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    public List<Category> getAll(Pageable pageable) {
        return categoriesRepository.findAll(pageable).toList();
    }

    public List<Category> getByTitle(String title, Pageable pageable) {
        return categoriesRepository.findByTitleContainsIgnoreCase(title, pageable).toList();
    }

    public Category getOne(Long id) {
        if (!categoriesRepository.existsById(id)) {
            throw new EntityNotFoundException("Unable to find Auction with id " + id);
        }
        return categoriesRepository.getReferenceById(id);
    }

    public Category create(CategoryDTO categoryDTO) {
        Category newCategory = new Category(categoryDTO);
        return categoriesRepository.save(newCategory);
    }

    public Category update(Long id, CategoryDTO categoryDTO) {
        Category existCategory = categoriesRepository.getReferenceById(id);

        if (categoryDTO.title() != null && !categoryDTO.title().equals(existCategory.getTitle())) {
            existCategory.setTitle(categoryDTO.title());
        }

        if (categoryDTO.description() != null && !categoryDTO.description().equals(existCategory.getDescription())) {
            existCategory.setDescription(categoryDTO.description());
        }

        return categoriesRepository.save(existCategory);
    }

    public boolean delete(Long id) {
        Category category = categoriesRepository.getReferenceById(id);
        if (!category.getLots().isEmpty()) {
            throw new UnableToDeleteException("Category cannot be deleted. You must delete or change the category of lots that have this category");
        }

        categoriesRepository.delete(category);
        return true;
    }

    public void addLotToCategory(Lot lot) {
        Category category = lot.getCategory();
        List<Lot> lots = category.getLots() == null ? new ArrayList<>() : category.getLots();
        lots.add(lot);
        category.setLots(lots);
        categoriesRepository.save(category);
    }

    public void unsetLotFromCategory(Lot lot) {
        Category category = lot.getCategory();
        List<Lot> lots = category.getLots();
        lots.remove(lot);
        category.setLots(lots);
        categoriesRepository.save(category);
    }
}