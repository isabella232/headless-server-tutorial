package com.coremedia.blueprint.contenthub.adapters.headlessdam.headless;

/**
 *
 */
public class SearchQuery {
  private static final String TEMPLATE = "{\"query\" : \"query {\n" +
          "  content {\n" +
          "    search(query: \\\"*\\\", offset: 0, limit: 1000, docTypes: [\\\"'type'\\\"], sortFields: [MODIFICATION_DATE_ASC], siteId: \\\"'siteId'\\\") {\n" +
          "     numFound,\n" +
          "     result {\n" +
          "       ... on CMTeasable {\n" +
          "          creationDate, \n" +
          "          name,           \n" +
          "          title,           \n" +
          "          type,           \n" +
          "          link {id},         \n" +
          "          picture {            \n" +
          "            data { \n" +
          "              size ,\n" +
          "              contentType\n" +
          "            },\n" +
          "            uriTemplate,\n" +
          "            crops {\n" +
          "              name,\n" +
          "              minWidth              \n" +
          "            }          \n" +
          "          }\n" +
          "        }\n" +
          "      }\n" +
          "    }\n" +
          "  }\n" +
          "}\" }";

  private String siteId;
  private String type;

  public SearchQuery(String siteId, String type) {
    this.siteId = siteId;
    this.type = type;
  }

  @Override
  public String toString() {
    return TEMPLATE.replaceAll("'siteId'", siteId).replaceAll("'type'", type);
  }
}
