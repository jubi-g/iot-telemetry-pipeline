## Stability & Testing

The system includes both **unit** and **integration** tests.

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
