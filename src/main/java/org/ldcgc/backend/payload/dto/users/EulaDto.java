package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.util.common.EEULAStatus;

import java.io.Serializable;
import java.util.List;

@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EulaDto implements Serializable {

    String url;
    List<EEULAStatus> actionsAvailable;

}
