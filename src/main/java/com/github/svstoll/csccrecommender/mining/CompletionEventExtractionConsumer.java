package com.github.svstoll.csccrecommender.mining;

import cc.kave.commons.model.events.completionevents.CompletionEvent;

import java.util.List;

@FunctionalInterface
public interface CompletionEventExtractionConsumer {

  void consume(List<CompletionEvent> events);
}
