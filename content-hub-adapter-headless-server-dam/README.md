# Implementing a Content Hub Adapter for CoreMedia Assets

## Introduction

This tutorial describes how to implement a CoreMedia Content Hub adapter that connects with the CoreMedia headless server.
It shows how the headless server can be used from external systems to serve CoreMedia content.
For the sake of this tutorial, we are "simulate" an asset management system using a CoreMedia itself which accesses the assets served by the headless server
through the CoreMedia Content Hub.

![Headless Server in Studio Library](../images/headless-dam-library.png "Headless Server in Studio Library")

## Prerequisites

This tutorial assumes that you have already developed with the CoreMedia Blueprint workspace.
It also assumes that you already know what the CoreMedia Content Hub is does and how it works.

## Implementation Steps

### Step 1: Project Setup



```yaml
{
  content {
    search(query: "*", offset: 0, limit: 10, docTypes: ["CMPicture"], sortFields: [MODIFICATION_DATE_ASC], siteId: "abffe57734feeee") {
      numFound
      result {
        ... on CMTeasable {
          creationDate, 
          title,           
          teaserText,
          remoteLink,
          picture {            
            data { 
              size ,
              contentType
            },
            uriTemplate,
            crops {
              name,
              minWidth              
            }          
          }
        }
      }
    }
  }
}
```

