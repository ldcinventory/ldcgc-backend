package org.ldcgc.backend.db.repository.history;

import org.ldcgc.backend.db.model.history.ConsumableRegister;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsumableRegisterRepository extends JpaRepository<ConsumableRegister, Integer> {

    List<ConsumableRegister> findAllByConsumable_Barcode(String barcode);

}
