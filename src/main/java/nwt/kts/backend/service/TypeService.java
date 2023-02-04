package nwt.kts.backend.service;

import nwt.kts.backend.entity.Type;
import nwt.kts.backend.repository.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TypeService {

    @Autowired
    private TypeRepository typeRepository;

    public Type findTypeByName(String name){
        return typeRepository.findTypeByName(name);
    }
}
