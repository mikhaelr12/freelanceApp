package com.freelance.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.freelance.app.repository.SkillRepository;
import com.freelance.app.service.dto.SkillShortDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Test
    void getAllSkillsShouldReturnShortDtosFromRepository() {
        SkillShortDTO firstSkill = new SkillShortDTO(1L, "Java");
        SkillShortDTO secondSkill = new SkillShortDTO(2L, "Spring");

        when(skillRepository.findAllShort()).thenReturn(Flux.just(firstSkill, secondSkill));

        SkillService skillService = new SkillService(skillRepository);
        List<SkillShortDTO> result = skillService.getAllSkills().block();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(firstSkill, secondSkill);

        verify(skillRepository).findAllShort();
    }

    @Test
    void getAllSkillsForCategoryShouldReturnCategorySkillsFromRepository() {
        Long categoryId = 1001L;
        SkillShortDTO firstSkill = new SkillShortDTO(3L, "Core Writing & Translation");
        SkillShortDTO secondSkill = new SkillShortDTO(4L, "Advanced Writing & Translation");

        when(skillRepository.findAllByCategoryShort(categoryId)).thenReturn(Flux.just(firstSkill, secondSkill));

        SkillService skillService = new SkillService(skillRepository);
        List<SkillShortDTO> result = skillService.getAllSkillsForCategory(categoryId).block();

        assertThat(result).isEqualTo(List.of(firstSkill, secondSkill));

        verify(skillRepository).findAllByCategoryShort(categoryId);
    }
}
