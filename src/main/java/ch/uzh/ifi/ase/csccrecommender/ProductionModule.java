package ch.uzh.ifi.ase.csccrecommender;

import com.google.inject.AbstractModule;

public class ProductionModule extends AbstractModule {

  @Override
  protected void configure() {
    // Add bindings for dependency injection here
    // bind(Communicator.class).to(DefaultCommunicatorImpl.class); NOSONAR
  }
}
