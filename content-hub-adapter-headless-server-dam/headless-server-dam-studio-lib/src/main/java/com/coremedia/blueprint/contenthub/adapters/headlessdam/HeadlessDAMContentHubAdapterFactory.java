package com.coremedia.blueprint.contenthub.adapters.headlessdam;

import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 *
 */
class HeadlessDAMContentHubAdapterFactory implements ContentHubAdapterFactory<HeadlessDAMContentHubConfiguration> {

  HeadlessDAMContentHubAdapterFactory() {
  }

  @Override
  @NonNull
  public String getId() {
    return "headlessdam";
  }

  @Override
  @NonNull
  public ContentHubAdapter createAdapter(@NonNull HeadlessDAMContentHubConfiguration settings,
                                         @NonNull String connectionId) {
    return new HeadlessDAMContentHubAdapter(settings, connectionId);
  }
}
