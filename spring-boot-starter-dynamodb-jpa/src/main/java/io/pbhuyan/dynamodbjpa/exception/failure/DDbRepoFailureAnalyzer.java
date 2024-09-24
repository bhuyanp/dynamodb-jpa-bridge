package io.pbhuyan.dynamodbjpa.exception.failure;

import io.pbhuyan.dynamodbjpa.exception.DDbRepoException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class DDbRepoFailureAnalyzer extends
        AbstractFailureAnalyzer<DDbRepoException> {


    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, DDbRepoException cause) {
        return new FailureAnalysis("DDbRepoException: "+cause.getMessage(), cause.getAction(), cause);    }
}
