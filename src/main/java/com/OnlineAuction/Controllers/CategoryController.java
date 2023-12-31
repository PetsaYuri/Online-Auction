package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.CategoryDTO;
import com.OnlineAuction.Models.Category;
import com.OnlineAuction.Services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAll(@RequestParam(value = "size", defaultValue = "10") int size, @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "q", required = false) String query) {
        Pageable pageable = PageRequest.of(page, size);
        return query != null ? categoryService.getByTitle(query, pageable) : categoryService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public Category getOne(@PathVariable("id") Long id) {
        return categoryService.getOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category create(@RequestBody CategoryDTO categoryDTO) {
        return categoryService.create(categoryDTO);
    }

    @PutMapping("/{id}")
    public Category update(@PathVariable("id") Long id, @RequestBody CategoryDTO categoryDTO) {
        return categoryService.update(id, categoryDTO);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return categoryService.delete(id);
    }
}