# OpenSearch Result Formatting Plugin

This OpenSearch plugin provides custom REST endpoints for formatting search responses in different formats. Originally migrated from Elasticsearch, this plugin now supports OpenSearch 3.3.0+ and offers enhanced result formatting capabilities.

## Features

This plugin extends OpenSearch with two specialized search endpoints:

1. **Array Format**: Returns search results as a simple JSON array
2. **FHIR Bundle Format**: Returns search results wrapped in a FHIR Bundle structure

## API Endpoints

### Array Format Endpoints
```
GET /_search_array
POST /_search_array
GET /{index}/_search_array
POST /{index}/_search_array
GET /{index}/{type}/_search_array
POST /{index}/{type}/_search_array
```

### FHIR Bundle Format Endpoints
```
GET /_search_fhir_bundle
POST /_search_fhir_bundle
GET /{index}/_search_fhir_bundle
POST /{index}/_search_fhir_bundle
GET /{index}/{type}/_search_fhir_bundle
POST /{index}/{type}/_search_fhir_bundle
```

## Requirements

- **Java**: JDK 21 or higher
- **OpenSearch**: Version 3.3.0 or higher
- **Gradle**: 8.10.2+ (included via wrapper)

## Installation

### Building from Source

1. **Clone and build the plugin**:
   ```bash
   git clone <repository-url>
   cd opensearch-result-formatting-plugin
   ./gradlew clean build
   ```

2. **Install the plugin**:
   ```bash
   # Navigate to your OpenSearch installation directory
   cd /path/to/opensearch
   
   # Install the plugin using file URI (Windows example)
   bin/opensearch-plugin install "file:///C:/path/to/opensearch-result-formatting-plugin/build/distributions/opensearch-result-formatting-1.0.0.zip"
   
   # Or for Linux/macOS
   bin/opensearch-plugin install "file:///path/to/opensearch-result-formatting-plugin/build/distributions/opensearch-result-formatting-1.0.0.zip"
   ```

3. **Verify installation**:
   ```bash
   bin/opensearch-plugin list
   ```
   You should see `opensearch-result-formatting` in the list.

4. **Restart OpenSearch**:
   ```bash
   # Stop OpenSearch if running, then start it
   bin/opensearch
   ```

## Usage Examples

### Array Format Response

**Request**:
```bash
curl -X GET "localhost:9200/_search_array"
```

**Response**:
```json
[
  {
    "study": "test study A",
    "StudyUID": "1.2.4.5.5252",
    "age": "12"
  },
  {
    "study": "test study B",
    "StudyUID": "1.2.4.5.212",
    "age": "12"
  },
  {
    "study": "test study C",
    "StudyUID": "1.2.4.5.23512.23.125.125",
    "age": "35"
  }
]
```

### FHIR Bundle Format Response

**Request**:
```bash
curl -X GET "localhost:9200/_search_fhir_bundle"
```

**Response**:
```json
{
  "resourceType": "Bundle",
  "type": "searchset",
  "total": 3,
  "entry": [
    {
      "resource": {
        "study": "test study A",
        "StudyUID": "1.2.4.5.5252",
        "age": "12"
      },
      "search": {
        "mode": "match"
      }
    }
  ]
}
```

## Advanced Features

### Array Format with Field Filtering

You can filter specific fields using the `filterField` parameter:

```bash
curl -X GET "localhost:9200/_search_array?filterField=StudyUID"
```

This will return only the values of the specified field as an array.

### FHIR Bundle with Highlights

The FHIR Bundle format automatically includes search highlights in the `response.eTag` field when highlights are present in the search results.

## Development

### Running Tests

```bash
# Run unit tests
./gradlew test

# Run integration tests
./gradlew integTest

# Run all tests
./gradlew check
```

### Building for Different OpenSearch Versions

To build for a different OpenSearch version, update the `opensearch_version` in `build.gradle`:

```gradle
buildscript {
    ext {
        opensearch_version = System.getProperty("opensearch.version", "3.3.0-SNAPSHOT")
    }
}
```

## Migration from Elasticsearch

This plugin was originally developed for Elasticsearch and has been migrated to work with OpenSearch. Key changes include:

- Updated package imports from `org.elasticsearch.*` to `org.opensearch.*`
- Compatibility with OpenSearch 3.3.0+ API changes
- Updated build system from Maven to Gradle
- Enhanced error handling and response formatting

## Troubleshooting

### Plugin Installation Issues

If you encounter `UnknownHostException` during installation:
- Ensure you're using the correct file URI format: `file:///` (three slashes)
- Use forward slashes `/` instead of backslashes `\` in paths
- Try copying the plugin zip to the OpenSearch directory first

### Version Compatibility

This plugin is built for OpenSearch 3.3.0+. For other versions, you may need to:
- Update the `opensearch.version` in `build.gradle`
- Rebuild the plugin
- Check for API compatibility issues

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the test suite
6. Submit a pull request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE.txt](LICENSE.txt) file for details.

## References

- **Original Elasticsearch Plugin**: [elasticsearch-arrayformat](https://github.com/jprante/elasticsearch-arrayformat)
- **RamSoft Elasticsearch Plugin**: [ElasticSearch-Result-Formatting-Plugin](https://github.com/ramsoft-inc/ElasticSearch-Result-Formatting-Plugin)
- **OpenSearch Plugin Development**: [OpenSearch Plugin Development Guide](https://opensearch.org/docs/latest/plugin-development/)
- **FHIR Bundle Specification**: [HL7 FHIR Bundle](https://www.hl7.org/fhir/bundle.html)

## Changelog

### Version 1.0.0
- Initial OpenSearch version migrated from Elasticsearch
- Support for OpenSearch 3.3.0+
- Enhanced FHIR Bundle formatting with highlight support
- Improved error handling and response formatting
- Updated build system to Gradle