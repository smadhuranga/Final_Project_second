package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.SkillDTO;

public interface SkillService {
    int saveSkill(SkillDTO skillDTO);
    SkillDTO getSkillById(Long id);
}