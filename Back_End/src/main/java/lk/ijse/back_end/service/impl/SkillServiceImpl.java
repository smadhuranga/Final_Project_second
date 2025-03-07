package lk.ijse.back_end.service.impl;



import org.example.springwithjwt.dto.SkillDTO;
import org.example.springwithjwt.entity.Skill;
import org.example.springwithjwt.repo.SkillRepository;
import org.example.springwithjwt.service.SkillService;
import org.example.springwithjwt.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SkillServiceImpl implements SkillService {

    @Autowired
    private SkillRepository skillRepository;

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