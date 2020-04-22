package com.coremedia.blueprint.contenthub.adapters.headlessdam;


import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import edu.umd.cs.findbugs.annotations.NonNull;

abstract class HeadlessDAMContentHubObject implements ContentHubObject {

  private ContentHubObjectId hubId;
  private String name;

  HeadlessDAMContentHubObject(String name, ContentHubObjectId hubId) {
    this.hubId = hubId;
    this.name = name;
  }

  @NonNull
  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @NonNull
  @Override
  public String getDisplayName() {
    return getName();
  }

  @NonNull
  @Override
  public ContentHubObjectId getId() {
    return hubId;
  }
}
