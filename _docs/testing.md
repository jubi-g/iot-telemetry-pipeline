## Stability & Testing

The system includes both **unit** and **integration** tests.

TODO: add more integrations tests for edge cases, failure paths, and validations

### Running Tests

```bash
  # Run all tests (unit + integration)
  mvn clean test
```
```bash
  # Run unit tests
  mvn test -DexcludedGroups=integration
```
```bash
  # Run integration tests
  mvn test -Dgroups=integration
```
