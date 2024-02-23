package org.ldcgc.backend.db.repository.history;

import org.ldcgc.backend.db.model.history.ConsumableRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ConsumableRegisterRepository extends JpaRepository<ConsumableRegister, Integer>, JpaSpecificationExecutor<ConsumableRegister> {

    List<ConsumableRegister> findAllByConsumable_Barcode(String barcode);

}
