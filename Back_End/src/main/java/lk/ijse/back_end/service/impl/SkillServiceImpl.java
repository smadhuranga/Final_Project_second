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

    @Autowired
    private SkillRepo skillRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public int saveSkill(SkillDTO skillDTO) {
        if (skillRepository.existsByName(skillDTO.getName())) {
            return VarList.Not_Acceptable;
        }
        skillRepository.save(modelMapper.map(skillDTO, Skill.class));
        return VarList.Created;
    }

    @Override
    public SkillDTO getSkillById(Long id) {
        return skillRepository.findById(id)
                .map(skill -> modelMapper.map(skill, SkillDTO.class))
                .orElse(null);
    }
}