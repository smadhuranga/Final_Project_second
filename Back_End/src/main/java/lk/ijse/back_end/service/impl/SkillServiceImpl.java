package lk.ijse.back_end.service.impl;

import lk.ijse.back_end.dto.SkillDTO;
import lk.ijse.back_end.entity.Skill;
import lk.ijse.back_end.repository.SkillRepo;
import lk.ijse.back_end.service.SkillService;
import lk.ijse.back_end.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SkillServiceImpl implements SkillService {

    private final SkillRepo skillRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SkillServiceImpl(SkillRepo skillRepository, ModelMapper modelMapper) {
        this.skillRepository = skillRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public int saveSkill(SkillDTO skillDTO) {
        if (skillRepository.existsByName(skillDTO.getName())) {
            return VarList.Not_Acceptable;
        }
        Skill skillEntity = modelMapper.map(skillDTO, Skill.class);
        skillRepository.save(skillEntity);
        return VarList.Created;
    }

    @Override
    public SkillDTO getSkillById(Long id) {
        return skillRepository.findById(id)
                .map(skill -> modelMapper.map(skill, SkillDTO.class))
                .orElse(null);
    }
}