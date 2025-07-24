package com.ssrmtech.itcompany.service.impl;

import com.ssrmtech.itcompany.model.ParentAdmin;
import com.ssrmtech.itcompany.repository.ParentAdminRepository;
import com.ssrmtech.itcompany.service.ParentAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParentAdminServiceImpl implements ParentAdminService {
    private final ParentAdminRepository parentAdminRepository;

    @Override
    public List<ParentAdmin> getAllParentAdmins() {
        return parentAdminRepository.findAll();
    }
}