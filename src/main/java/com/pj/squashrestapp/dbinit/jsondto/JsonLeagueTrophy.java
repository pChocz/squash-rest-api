package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.dto.Trophy;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonLeagueTrophy {

  private int seasonNumber;

  private UUID playerUuid;

  private Trophy trophy;

}
