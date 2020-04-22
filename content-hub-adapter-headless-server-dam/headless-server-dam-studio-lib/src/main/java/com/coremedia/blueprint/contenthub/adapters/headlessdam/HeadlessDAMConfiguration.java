package com.coremedia.blueprint.contenthub.adapters.headlessdam;

import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HeadlessDAMConfiguration {

  @Bean
  public ContentHubAdapterFactory coreMediaContentHubAdapterFactory() {
    return new HeadlessDAMContentHubAdapterFactory();
  }
}
