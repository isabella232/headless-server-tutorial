package com.coremedia.blueprint.contenthub.adapters.headlessdam.headless;

import java.util.List;

/**
 *
 */
public class PictureDocument {
  private String uriTemplate;
  private List<CropDocument> crops;
  private BlobDocument data;

  public String getUriTemplate() {
    return uriTemplate;
  }

  public void setUriTemplate(String uriTemplate) {
    this.uriTemplate = uriTemplate;
  }

  public List<CropDocument> getCrops() {
    return crops;
  }

  public void setCrops(List<CropDocument> crops) {
    this.crops = crops;
  }

  public BlobDocument getData() {
    return data;
  }

  public void setData(BlobDocument data) {
    this.data = data;
  }

  public String getDefaultImageUrl(String headlessServerUrl) {
    CropDocument cropDocument = crops.get(0);
    String variant = cropDocument.getName();
    int size = cropDocument.getMinWidth();

    String url = uriTemplate.replace("{cropName}", variant);
    url = url.replace("{width}", String.valueOf(size));

    if (headlessServerUrl.endsWith("/")) {
      headlessServerUrl = headlessServerUrl.substring(0, headlessServerUrl.length() - 1);
    }

    return headlessServerUrl + url;
  }
}
