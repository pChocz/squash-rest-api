package com.pj.squashrestapp.mongologs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("logEntries")
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class LogEntry {

    @Id
    private String id;

    private Date timestamp;
    private String username;
    private String className;
    private String methodName;
    private String requestMapping;
    private String arguments;
    private Long duration;
    private Long queryCount;
    private Boolean isException;
    private String message;
    private LogType type;
}
