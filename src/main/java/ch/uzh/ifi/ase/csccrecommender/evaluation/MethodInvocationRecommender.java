package ch.uzh.ifi.ase.csccrecommender.evaluation;

import cc.kave.commons.model.events.completionevents.Context;
import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndex;
import ch.uzh.ifi.ase.csccrecommender.mining.ContextExtractor;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContext;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContextVisitor;

import com.google.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class MethodInvocationRecommender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodInvocationRecommender.class);

    private final MethodInvocationIndex methodInvocationIndex;
    private final ContextExtractor contextExtractor;

    @Inject
    protected MethodInvocationRecommender(MethodInvocationIndex methodInvocationIndex,
                                          ContextExtractor contextExtractor) {
        this.methodInvocationIndex = methodInvocationIndex;
        this.contextExtractor = contextExtractor;
    }

    void recommend4AllInvocationsInAvailableContexts() {
        // make sure the tmp folder is empty
        try {
            FileUtils.deleteDirectory(new File("./tmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        contextExtractor.processAllContexts(contexts -> {
            long start = System.currentTimeMillis();
            R4InvocationLineContextVisitor r4InvocationLineContextVisitor = new R4InvocationLineContextVisitor(methodInvocationIndex);
            for (Context context : contexts) {
                context.getSST().accept(new CsccContextVisitor(), new CsccContext(r4InvocationLineContextVisitor));
            }
            long end = System.currentTimeMillis();
            LOGGER.info("SST traversals with document recommendation took {} ms.", end - start);
        });

    }
}
