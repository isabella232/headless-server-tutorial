package com.coremedia.blueprint.contenthub.adapters.headlessdam;

import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Folder;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class HeadlessDAMFolder extends HeadlessDAMContentHubObject implements Folder {
  public static final String ROOT_FOLDER = "root";
  public static final String AUDIO_FOLDER = "audio";
  public static final String VIDEO_FOLDER = "video";
  public static final String PICTURES_FOLDER = "pictures";

  private final ContentHubType type;

  HeadlessDAMFolder(String name, ContentHubObjectId id, @Nullable ContentHubType type) {
    super(name, id);
    this.type = type;
  }

  @NonNull
  @Override
  public ContentHubType getContentHubType() {
    return type==null ? Folder.super.getContentHubType() : type;
  }
}
