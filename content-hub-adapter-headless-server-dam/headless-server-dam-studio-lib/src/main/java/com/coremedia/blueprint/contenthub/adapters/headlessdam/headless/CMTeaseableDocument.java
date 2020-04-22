package com.coremedia.blueprint.contenthub.adapters.headlessdam.headless;

/**
 *
 */
public class CMTeaseableDocument {
  private CMTeaseableDocument content;

  private String name;
  private String title;
  private String teaserText;
  private String remoteLink;
  private PictureDocument picture;
  private LinkDocument link;
  private String type;

  public CMTeaseableDocument getContent() {
    return content;
  }

  public void setContent(CMTeaseableDocument content) {
    this.content = content;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTeaserText() {
    return teaserText;
  }

  public void setTeaserText(String teaserText) {
    this.teaserText = teaserText;
  }

  public PictureDocument getPicture() {
    return picture;
  }

  public void setPicture(PictureDocument picture) {
    this.picture = picture;
  }

  public String getRemoteLink() {
    return remoteLink;
  }

  public void setRemoteLink(String remoteLink) {
    this.remoteLink = remoteLink;
  }

  public LinkDocument getLink() {
    return link;
  }

  public void setLink(LinkDocument link) {
    this.link = link;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
