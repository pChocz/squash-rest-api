package com.pj.squashrestapp.model.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Audit {

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "created_by")
    private String createdBy;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "updated_by")
    private String updatedBy;
}
