package com.coremedia.blueprint.contenthub.adapters.headlessdam;


import com.coremedia.blueprint.contenthub.adapters.headlessdam.headless.CMTeaseableDocument;
import com.coremedia.contenthub.api.ContentHubBlob;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.UrlBlobBuilder;
import com.coremedia.contenthub.api.preview.DetailsElement;
import com.coremedia.contenthub.api.preview.DetailsSection;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class HeadlessDAMItem extends HeadlessDAMContentHubObject implements Item {
  private CMTeaseableDocument content;
  private String headlessServerUrl;

  HeadlessDAMItem(CMTeaseableDocument content, ContentHubObjectId id, String headlessServerUrl) {
    super(content.getName(), id);
    this.content = content;
    this.headlessServerUrl = headlessServerUrl;
  }

  @Nullable
  @Override
  public String getDescription() {
    return content.getTitle();
  }

  @NonNull
  @Override
  public ContentHubType getContentHubType() {
    return new ContentHubType(content.getType());
  }

  @NonNull
  @Override
  public String getCoreMediaContentType() {
    return content.getType();
  }

  @NonNull
  @Override
  public List<DetailsSection> getDetails() {
    if (content.getType().equals("CMPicture")){
      return pictureDetails();
    }

    return contentDetails();
  }

  private List<DetailsSection> contentDetails() {
    List<DetailsElement<?>> elements = List.of(new DetailsElement<>(content.getName(), SHOW_TYPE_ICON));
    return List.of(new DetailsSection("main", elements, false, false , false), getMetaDataSection());
  }

  private List<DetailsSection> pictureDetails() {
    String pictureUrl = content.getPicture().getDefaultImageUrl(headlessServerUrl);
    ContentHubBlob picture = new UrlBlobBuilder(this, "preview").withUrl(pictureUrl).withEtag().build();
    List<DetailsElement<?>> elements = List.of(new DetailsElement<>(content.getName(), false, picture));
    return List.of(new DetailsSection("main", elements, false, false, false), getMetaDataSection());
  }

  @Nullable
  @Override
  public ContentHubBlob getBlob(String classifier) {
    String pictureUrl = content.getPicture().getDefaultImageUrl(headlessServerUrl);
    return new UrlBlobBuilder(this, classifier).withUrl(pictureUrl).withEtag().build();
  }

  @NonNull
  private DetailsSection getMetaDataSection() {
    return new DetailsSection("metadata", List.of(
            new DetailsElement<>("name", content.getName()),
            new DetailsElement<>("id", content.getLink().getId()),
            new DetailsElement<>("type", content.getType())
    ).stream().filter(p -> Objects.nonNull(p.getValue())).collect(Collectors.toUnmodifiableList()));
  }
}
