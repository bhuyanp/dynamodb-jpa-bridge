package io.pbhuyan.dynamodbjpa.exception;

import org.springframework.beans.factory.BeanCreationException;

public class DDbConfigException extends DDbException {
    public DDbConfigException(String msg) {
        super(msg);
    }
}
