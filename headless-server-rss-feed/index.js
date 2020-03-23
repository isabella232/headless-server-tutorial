var http = require('http');
var fetch = require("node-fetch");
var RSS = require('rss');

var headlessServerUrl = "https://headless-server-preview.sherlock-labs.testsystem.coremedia.io/";
console.log("Using headless server: " + headlessServerUrl);

function fetchFeedData() {
  return fetch(headlessServerUrl + 'graphql', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    body: JSON.stringify({
      query: "{\n" +
              "  content {\n" +
              "    search(query: \"*\", offset: 3, limit: 10, docTypes: [\"CMArticle\"], sortFields: [MODIFICATION_DATE_ASC], siteId: \"abffe57734feeee\") {\n" +
              "      numFound\n" +
              "      result {\n" +
              "        ... on CMArticle {\n" +
              "          creationDate, \n" +
              "          title,           \n" +
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
              "            },            \n" +
              "          }\n" +
              "        }\n" +
              "      }\n" +
              "    }\n" +
              "  }\n" +
              "}"
    })
  })
          .then(r => r.json())
          .then(data => {
            console.log('data returned:', data.data.content.search.result.length);
            return data.data.content.search.result;
          });
}


function toFeedXML(data) {
  //create the feed itself
  var feed = new RSS({
    title: 'Headless RSS Feed',
    description: 'RSS feed build from CoreMedia Headless Server data',
    feed_url: 'http://localhost:8080',
    language: 'en'
  });

  //iterate over the article records and create feed items
  for (let i = 0; i < data.length; i++) {
    var entry = data[i];
    feed.item({
      title:  entry.title,
      description: entry.teaserText,
      date: entry.creationDate.substr(0, 19),
      url: 'https:' + entry.remoteLink,
      enclosure: {
        'url'  : formatPictureUrl(entry.picture),
        'type' : entry.picture.data.contentType,
        'size' : entry.picture.data.size
      }
    });
  }

  //return the RSS XML
  return feed.xml();
}

function formatPictureUrl(picture) {
  let url = picture.uriTemplate.replace('{cropName}', picture.crops[0].name).replace('{width}', picture.crops[0].minWidth);
  return headlessServerUrl + url;
}


http.createServer(function (req, res) {
  fetchFeedData().then(data => {
    let xml = toFeedXML(data);
    res.writeHead(200, {'Content-Type': 'application/rss+xml'});
    res.end(xml);
  });
}).listen(8081);
console.log("Started HTTP Server (http://localhost:8081)");