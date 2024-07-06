package com.cnam.options;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Data;

import java.util.StringJoiner;

@Parameters(separators = "=")
@Data
public class Options {

    @Parameter(names = "scheduledPoolSize")
    private Integer scheduledPoolSize;

    @Parameter(names = "priorityPoolSize")
    private Integer priorityPoolSize;

    @Parameter(names = "heavyPoolSize")
    private Integer heavyPoolSize;


    @Override
    public String toString() {
        String newLine = System.lineSeparator();
        return new StringJoiner("," + newLine, Options.class.getSimpleName() + ":" + newLine, "")
                .add("scheduledPoolSize=" + scheduledPoolSize)
                .add("priorityPoolSize=" + priorityPoolSize)
                .add("heavyPoolSize=" + heavyPoolSize)
                .toString();
    }
}
