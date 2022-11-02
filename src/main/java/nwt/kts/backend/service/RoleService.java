package nwt.kts.backend.service;

import nwt.kts.backend.entity.Role;
import nwt.kts.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role findRoleByName(String name){
        return roleRepository.findRoleByName(name);
    }
}
