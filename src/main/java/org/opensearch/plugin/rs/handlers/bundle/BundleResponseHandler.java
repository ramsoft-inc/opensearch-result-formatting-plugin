/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.plugin.rs.handlers.bundle;

import org.opensearch.action.search.SearchResponse;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestChannel;
import org.opensearch.rest.RestResponse;
import org.opensearch.rest.action.RestResponseListener;
import org.opensearch.search.SearchHit;
import org.opensearch.search.fetch.subphase.highlight.HighlightField;
import org.opensearch.core.xcontent.XContentBuilder;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BundleResponseHandler extends RestResponseListener<SearchResponse> {

    public BundleResponseHandler(RestChannel channel) {
        super(channel);
    }

    @Override
    public RestResponse buildResponse(SearchResponse searchResponse) throws Exception {
        return buildResponse(searchResponse, channel.newBuilder());
    }

    private RestResponse buildResponse(SearchResponse response, XContentBuilder builder) throws Exception {
        int total = 0;
        ArrayList entryList = new ArrayList();
        builder.startObject();
        builder.field("resourceType", "Bundle");
        builder.field("type", "searchset");

        if (response.getHits() != null && response.getHits().getHits() != null && response.getHits().getHits().length > 0) {
            for (SearchHit hit : response.getHits().getHits()) {
                Map entryBuilder = new HashMap();
                Map sourceMap = hit.getSourceAsMap();

                // add resource from FhirJSON
                Object fhirJson = sourceMap.get("FhirJSON");
                if(fhirJson != null) {
                    JSONObject fhirJsonObject = new JSONObject(fhirJson.toString());
                    entryBuilder.put("resource", fhirJsonObject.toMap());
                } else {
                    entryBuilder.put("resource", sourceMap);
                }

                // add search
                Map searchMap = new HashMap();
                searchMap.put("mode", "match");
                entryBuilder.put("search", searchMap);

                // add highlight array to $.entry[].response.eTag
                if (hit.getHighlightFields() != null && !hit.getHighlightFields().isEmpty())
                {
                    Map highlightBuilder = new HashMap();
                    for (HighlightField value : hit.getHighlightFields().values()) {
                        highlightBuilder.put(value.getName(), Arrays.toString(value.getFragments()));
                    }

                    Map eTagMap = new HashMap();
                    eTagMap.put("eTag", new JSONObject(highlightBuilder).toString());
                    entryBuilder.put("response", eTagMap);
                }

                entryList.add(entryBuilder);
                total++;
            }
        }

        builder.field("total", total);
        builder.field("entry", entryList);
        builder.endObject();
        return new BytesRestResponse(response.status(), builder);
    }
}
