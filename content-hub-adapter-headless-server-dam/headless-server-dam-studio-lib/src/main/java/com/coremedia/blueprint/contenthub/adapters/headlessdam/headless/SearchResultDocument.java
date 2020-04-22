package com.coremedia.blueprint.contenthub.adapters.headlessdam.headless;

import java.util.List;

/**
 *
 */
public class SearchResultDocument {
  private int numFound;
  private List<CMTeaseableDocument> result;

  public int getNumFound() {
    return numFound;
  }

  public void setNumFound(int numFound) {
    this.numFound = numFound;
  }

  public List<CMTeaseableDocument> getResult() {
    return result;
  }

  public void setResult(List<CMTeaseableDocument> result) {
    this.result = result;
  }
}
