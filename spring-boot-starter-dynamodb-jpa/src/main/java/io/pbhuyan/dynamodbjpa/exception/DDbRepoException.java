package io.pbhuyan.dynamodbjpa.exception;

import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

@Getter
public class DDbRepoException extends DDbException {
    private final String action;
    public DDbRepoException(String msg, String action) {
        super(msg);
        this.action = action;
    }

    public DDbRepoException(String msg) {
        super(msg);
        this.action = Strings.EMPTY;
    }
}
