/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.plugin.rs.handlers.array;

import org.opensearch.action.search.SearchResponse;
import org.opensearch.rest.RestRequest;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.rest.RestChannel;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestResponse;
import org.opensearch.rest.action.RestResponseListener;
import org.opensearch.search.SearchHit;

import java.util.Map;

public class ArrayResponseHandler extends RestResponseListener<SearchResponse> {

    public ArrayResponseHandler(RestChannel channel) {
        super(channel);
    }

    @Override
    public RestResponse buildResponse(SearchResponse searchResponse) throws Exception {
        RestRequest request = channel.request();
        String uri = request.uri();
        String filterField = null;
        if(uri.contains("filterField")) {
            String[] urlPath = uri.split("=");
            filterField = urlPath.length > 1 ? urlPath[1] : null;
        }

        return buildResponse(searchResponse, channel.newBuilder(), filterField);
    }

    private RestResponse buildResponse(SearchResponse response, XContentBuilder builder, String filterField) throws Exception {
        if (response.getHits() == null || response.getHits().getHits() == null || response.getHits().getHits().length == 0) {
            builder.startArray().endArray();
        } else {
            builder.startArray();
            for (SearchHit hit : response.getHits().getHits()) {
                Map sourceMap = hit.getSourceAsMap();

                if(filterField != null && filterField.trim().length() > 0) {
                    Object value = sourceMap.get(filterField);
                    if(value != null) {
                        builder.value(value);
                    } else {
                        builder.map(sourceMap);
                    }
                } else {
                    builder.map(sourceMap);
                }
            }
            builder.endArray();
        }
        return new BytesRestResponse(response.status(), builder);
    }
}
