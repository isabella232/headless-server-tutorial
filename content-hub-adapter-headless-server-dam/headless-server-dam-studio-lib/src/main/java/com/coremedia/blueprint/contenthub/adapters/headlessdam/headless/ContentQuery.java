package com.coremedia.blueprint.contenthub.adapters.headlessdam.headless;

/**
 *
 */
public class ContentQuery {
  private static final String TEMPLATE = "{\"query\" : \"query {\n" +
          "  content {\n" +
          "    content(id:\\\"'id'\\\") {\n" +
          "       ... on CMTeasable {\n" +
          "          creationDate, \n" +
          "          name,           \n" +
          "          title,           \n" +
          "          type,           \n" +
          "          link {id},         \n" +
          "          teaserText,\n" +
          "          remoteLink,\n" +
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
          "    }\n" +
          "  }\n" +
          "}\" }";

  private String id;

  public ContentQuery(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return TEMPLATE.replaceAll("'id'", id);
  }
}
