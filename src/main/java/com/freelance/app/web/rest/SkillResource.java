package com.freelance.app.web.rest;

import com.freelance.app.service.SkillService;
import com.freelance.app.service.dto.SkillShortDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing {@link com.freelance.app.domain.Skill}.
 */
@RestController
@RequestMapping("/api/skills")
public class SkillResource {

    private static final Logger LOG = LoggerFactory.getLogger(SkillResource.class);

    private static final String ENTITY_NAME = "skill";

    private final SkillService skillService;

    public SkillResource(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/{categoryId}")
    public Mono<ResponseEntity<List<SkillShortDTO>>> getAllSkillsForCategory(@PathVariable Long categoryId) {
        LOG.info("REST request to get all skills for category {}", categoryId);
        return skillService.getAllSkillsForCategory(categoryId).map(ResponseEntity::ok);
    }
}
