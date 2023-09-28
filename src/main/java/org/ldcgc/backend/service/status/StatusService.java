package org.ldcgc.backend.service.status;

import org.ldcgc.backend.util.common.EStatus;

public interface StatusService {

    int getIdByEStatus(EStatus name);
}
