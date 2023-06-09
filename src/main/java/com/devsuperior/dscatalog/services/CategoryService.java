package com.devsuperior.dscatalog.services;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

import javax.persistence.EntityNotFoundException;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(Pageable pageable) {
        Page<Category> list = categoryRepository.findAll(pageable);
        return list.map(x -> new CategoryDTO(x));
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Optional<Category> obj = categoryRepository.findById(id);
        Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("ID " + id + " not found!"));
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category entity = new Category();
        BeanUtils.copyProperties(dto, entity);
        categoryRepository.save(entity);
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category entity = categoryRepository.getOne(id);
            entity.setName(dto.getName());
            entity = categoryRepository.save(entity);
            return new CategoryDTO(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("ID "+ id + " not found!");
        }
    }

    public void delete(Long id) {
        try {
            categoryRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation, have product in this category!");
        }
    }

    public boolean LocateById(Long id) {
        if (!categoryRepository.existsById(id)) {
            return false;
        }
        else {
            return true;
        }
    }
}
