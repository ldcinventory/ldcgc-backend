package org.ldcgc.backend.service.status;

import org.ldcgc.backend.util.common.EStatus;
import org.springframework.stereotype.Service;

@Service
public interface StatusService {

    int getIdByEStatus(EStatus name);

}
