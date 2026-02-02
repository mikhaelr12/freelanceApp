package com.freelance.app.service;

import com.freelance.app.repository.SkillRepository;
import com.freelance.app.service.dto.SkillShortDTO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Skill}.
 */
@Service
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Mono<List<SkillShortDTO>> getAllSkillsForCategory(Long categoryId) {
        return skillRepository.findAllByCategoryShort(categoryId).collectList();
    }
}
