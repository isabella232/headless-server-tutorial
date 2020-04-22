package com.coremedia.blueprint.contenthub.adapters.headlessdam.headless;

/**
 *
 */
public class CMContentDocument {
  private CMTeaseableDocument content;
  private SearchResultDocument search;

  public CMTeaseableDocument getContent() {
    return content;
  }

  public void setContent(CMTeaseableDocument content) {
    this.content = content;
  }

  public SearchResultDocument getSearch() {
    return search;
  }

  public void setSearch(SearchResultDocument search) {
    this.search = search;
  }
}
