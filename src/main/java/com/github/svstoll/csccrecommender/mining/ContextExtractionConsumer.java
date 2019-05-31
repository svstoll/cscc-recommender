package com.github.svstoll.csccrecommender.mining;

import cc.kave.commons.model.events.completionevents.Context;

import java.util.List;

@FunctionalInterface
public interface ContextExtractionConsumer {

  void consume(List<Context> contexts);
}
