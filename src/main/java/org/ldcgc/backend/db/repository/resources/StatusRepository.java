package org.ldcgc.backend.db.repository.resources;

import org.ldcgc.backend.db.model.resources.Status;
import org.ldcgc.backend.util.common.EStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.net.ssl.SSLSession;
import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Integer> {

    Optional<Status> findByName(EStatus name);
}
