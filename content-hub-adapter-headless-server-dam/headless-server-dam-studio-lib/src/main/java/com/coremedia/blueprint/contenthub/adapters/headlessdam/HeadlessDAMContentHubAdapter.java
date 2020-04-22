package com.coremedia.blueprint.contenthub.adapters.headlessdam;

import com.coremedia.blueprint.contenthub.adapters.headlessdam.headless.CMContentDocument;
import com.coremedia.blueprint.contenthub.adapters.headlessdam.headless.CMTeaseableDocument;
import com.coremedia.blueprint.contenthub.adapters.headlessdam.headless.HeadlessServerConnector;
import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubContext;
import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubTransformer;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Folder;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.exception.ContentHubException;
import com.coremedia.contenthub.api.search.ContentHubSearchResult;
import com.coremedia.contenthub.api.search.ContentHubSearchService;
import com.coremedia.contenthub.api.search.Sort;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class HeadlessDAMContentHubAdapter implements ContentHubAdapter, ContentHubSearchService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private ContentHubObjectId rootId;
  private String connectionId;
  private String headlessServerUrl;
  private String siteId;

  private HeadlessDAMFolder rootFolder;
  private HeadlessDAMFolder audioFolder;
  private HeadlessDAMFolder videoFolder;
  private HeadlessDAMFolder picturesFolder;

  private HeadlessServerConnector headlessServerConnector;

  HeadlessDAMContentHubAdapter(@NonNull HeadlessDAMContentHubConfiguration settings, String connectionId) {
    try {
      this.connectionId = connectionId;
      this.headlessServerUrl = settings.getHeadlessServerUrl();
      this.siteId = settings.getSiteId();

      if(headlessServerUrl == null) {
        throw new IllegalArgumentException("No headlessServerUrl set for content hub adapter connection '" + connectionId + "'");
      }

      if(siteId == null) {
        throw new IllegalArgumentException("No siteId set for content hub adapter connection '" + connectionId + "'");
      }

      if(!headlessServerUrl.endsWith("/")) {
        headlessServerUrl = headlessServerUrl + "/";
      }

      rootId = new ContentHubObjectId(connectionId, HeadlessDAMFolder.ROOT_FOLDER);
      rootFolder = new HeadlessDAMFolder(headlessServerUrl, rootId, new ContentHubType("headlessdam"));

      ContentHubObjectId audioId = new ContentHubObjectId(connectionId, HeadlessDAMFolder.AUDIO_FOLDER);
      audioFolder = new HeadlessDAMFolder("Audio", audioId, new ContentHubType("headlessdam"));

      ContentHubObjectId videoId = new ContentHubObjectId(connectionId, HeadlessDAMFolder.VIDEO_FOLDER);
      videoFolder = new HeadlessDAMFolder("Videos", videoId, new ContentHubType("headlessdam"));

      ContentHubObjectId picturesId = new ContentHubObjectId(connectionId, HeadlessDAMFolder.PICTURES_FOLDER);
      picturesFolder = new HeadlessDAMFolder("Pictures", picturesId, new ContentHubType("headlessdam"));

      headlessServerConnector = new HeadlessServerConnector(headlessServerUrl);
    } catch (IllegalArgumentException e) {
      LOG.error("Failed to initialized adapter for Headless DAM: {}", e.getMessage());
      throw new ContentHubException("Failed to initialized content hub adapter for Headless DAM", e);
    }
  }

  @NonNull
  @Override
  public Folder getRootFolder(@NonNull ContentHubContext context) {
    return rootFolder;
  }

  @Nullable
  @Override
  public Item getItem(@NonNull ContentHubContext context, @NonNull ContentHubObjectId id) {
    String capId = id.getExternalId();
    CMContentDocument content = headlessServerConnector.getContent(capId);
    return new HeadlessDAMItem(content.getContent(), id, headlessServerUrl);
  }

  @Nullable
  @Override
  public Folder getFolder(@NonNull ContentHubContext context, @NonNull ContentHubObjectId id) {
    String externalId = id.getExternalId();
    switch (externalId) {
      case HeadlessDAMFolder.ROOT_FOLDER:
        return rootFolder;
      case HeadlessDAMFolder.AUDIO_FOLDER:
        return audioFolder;
      case HeadlessDAMFolder.VIDEO_FOLDER:
        return videoFolder;
      case HeadlessDAMFolder.PICTURES_FOLDER:
        return picturesFolder;
      default:
        throw new UnsupportedOperationException("Invalid headless DAM id '" + externalId + "'");
    }
  }

  @NonNull
  @Override
  public List<Item> getItems(@NonNull ContentHubContext context, @NonNull Folder folder) {
    String externalId = folder.getId().getExternalId();

    switch (externalId) {
      case HeadlessDAMFolder.ROOT_FOLDER:
        return Collections.emptyList();
      case HeadlessDAMFolder.AUDIO_FOLDER:
        CMContentDocument cmAudio = headlessServerConnector.search(siteId, "CMAudio");
        return toItems(cmAudio.getSearch().getResult());
      case HeadlessDAMFolder.VIDEO_FOLDER:
        CMContentDocument cmVideo = headlessServerConnector.search(siteId, "CMVideo");
        return toItems(cmVideo.getSearch().getResult());
      case HeadlessDAMFolder.PICTURES_FOLDER:
        CMContentDocument cmPicture = headlessServerConnector.search(siteId, "CMPicture");
        return toItems(cmPicture.getSearch().getResult());
      default:
        throw new UnsupportedOperationException("Invalid headless DAM id '" + externalId + "'");
    }
  }

  @NonNull
  @Override
  public List<Folder> getSubFolders(@NonNull ContentHubContext context, @NonNull Folder folder) {
    List<Folder> result = new ArrayList<>();
    String externalId = folder.getId().getExternalId();
    if (externalId.equals(HeadlessDAMFolder.ROOT_FOLDER)) {
      return Arrays.asList(audioFolder, videoFolder, picturesFolder);
    }
    return result;
  }

  @Nullable
  @Override
  public Folder getParent(@NonNull ContentHubContext context, @NonNull ContentHubObject contentHubObject) {
    String externalId = contentHubObject.getId().getExternalId();
    if (externalId.equals(HeadlessDAMFolder.ROOT_FOLDER)) {
      return null;
    }

    return rootFolder;
  }

  @Override
  @NonNull
  public ContentHubTransformer transformer() {
    throw new UnsupportedOperationException("To be implemented if needed");
  }

  @Override
  @NonNull
  public Optional<ContentHubSearchService> searchService() {
    return Optional.of(this);
  }

  @Override
  public Collection<ContentHubType> supportedTypes() {
    return Collections.emptyList();
  }

  @Override
  public ContentHubSearchResult search(String query,
                                       @Nullable Folder belowFolder,
                                       @Nullable ContentHubType type,
                                       Collection<String> filterQueries,
                                       List<Sort> sortBy,
                                       int limit) {
    return new ContentHubSearchResult(Collections.emptyList());
  }

  @Override
  public boolean supportsSearchBelowFolder() {
    return false;
  }

  private List<Item> toItems(List<CMTeaseableDocument> items) {
    List<Item> result = new ArrayList<>();
    for (CMTeaseableDocument item : items) {
      ContentHubObjectId id = new ContentHubObjectId(connectionId, item.getLink().getId());
      HeadlessDAMItem headlessDAMItem = new HeadlessDAMItem(item, id, headlessServerUrl);
      result.add(headlessDAMItem);
    }
    return result;
  }
}
