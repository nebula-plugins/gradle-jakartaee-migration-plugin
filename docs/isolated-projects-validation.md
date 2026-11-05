# Isolated Projects validation

Regression tests cover project-local migration workflows and publishing under strict configuration-cache and Isolated Projects checks. Production sources, plugin IDs, extension signatures, defaults and project participation are unchanged. Tests publish only fixture artifacts to temporary local Maven repositories.

## Reproduce

Run from the repository root with installed JDK 8 and JDK 21 homes:

```sh
JAVA8_HOME=/path/to/jdk8 JAVA21_HOME=/path/to/jdk21 ./gradlew test \
  --tests '*IsolatedProjectsSpec' \
  --tests '*PublishingIsolation*Spec' \
  --tests '*IsolationCustomizationCharacterizationSpec' \
  --tests '*MinimumGradleCharacterizationSpec' \
  --tests '*ConfigurationCacheSpec' --console=plain
```

The positive matrix pins Gradle **8.6, 9.6.1 and 9.8.2** and uses JDK 21 for the fixture daemons. It does not follow wrapper changes automatically. Gradle 8.6 and 9.6.1 use `-Dorg.gradle.unsafe.isolated-projects=true`; ordinary controls explicitly set it to `false`. Gradle 9.8.2 uses `--isolated-projects` and `--no-isolated-projects`, matching its CLI help and [versioned documentation](https://docs.gradle.org/9.8.2/userguide/isolated_projects.html#sec:enable_ip). Every positive matrix build uses `--configuration-cache --configuration-cache-problems=fail`, with storage and reuse assertions. No warn mode or ignored isolation problems are used. The isolated build must report that the feature is enabled.

The repository's existing `org.gradle.isolated-projects=true` property is the stable spelling for 9.7+, so it does not establish isolation of the repository build on its 9.6.1 wrapper. The TestKit fixtures explicitly select the correct switch for each version independently of that property. Validating the repository's own build-convention plugins under isolation is outside this consumer-plugin validation; adding 9.8.2 fixtures does not upgrade the repository wrapper.

The shared runner and the existing cache-invalidation regression use `--no-watch-fs` so each build rechecks files in these rapidly edited temporary fixtures. During validation on macOS, filesystem-watcher timing intermittently reused an entry immediately after a script edit, or reported that edit one build late. Disabling the watcher makes input invalidation deterministic without relaxing cache or isolation diagnostics. Watcher-enabled behavior is outside this final validation envelope.

The minimum characterization uses Gradle **4.0 on JDK 8**. It explicitly skips without `JAVA8_HOME`; a skipped test is not validation evidence. The existing CI setup installs JDKs 8 and 21; the build step now passes their homes to the tests, including when the test worker runs on another matrix JVM.

Recorded local result, 2026-11-05 on macOS arm64: **BUILD SUCCESSFUL; 60 tests, 0 failures, 0 errors, 0 skips**, including 16 cases targeting Gradle 9.8.2. The test worker and feature fixtures used OpenJDK 21.0.2; the Gradle 4.0 fixtures used Azul JDK 8.0.504. Seven of the passing cases characterize expected failures rather than compatibility. XML results are in `build/test-results/test/`; the HTML report is `build/reports/tests/test/index.html`.

## Coverage

| Spec | Cases | Evidence |
| --- | ---: | --- |
| `IsolatedProjectsSpec` | 36 | Single, multi and nested projects; local participation; substitution, capability selection, transformation, in-memory processing, overload distinctions, late source sets, cache replay and invalidation |
| `PublishingIsolationSpec` | 6 | Gradle 9.6.1 and 9.8.2: default application, automatic migration and runtime-only migration; ordinary/isolated publication and outgoing-variant equivalence |
| `PublishingIsolationCharacterizationSpec` | 2 | Gradle 8.6: child Maven publication isolation failure reproduced with and without the Jakarta plugin; ordinary cache storage/reuse still passes |
| `ConfigurationCacheSpec` | 11 | Existing cache regression coverage retained |
| `IsolationCustomizationCharacterizationSpec` | 3 | Expected isolation failures for consumer access to a foreign configuration; ordinary behavior retained |
| `MinimumGradleCharacterizationSpec` | 2 | Expected Gradle 4.0 feature and plugin-application failures |

The tests use local Maven fixtures and inspect JAR resource contents (`javax.mail.Session` becomes `jakarta.mail.Session`), not only transformed filenames. Real producer/consumer Java compilation exercises project dependency task wiring. Different projects retain independent exclusions; applying only at the root does not enroll children. A source set created after `migrate()` receives migration configuration.

Cache replay deletes the report to force task execution from the cached graph and compares the resolved contents. Changing `excludeTransform` invalidates configuration state and restores original dependency contents. Changing the provider-backed dependency version invalidates the cache and changes the selected artifact/content; stale versions disappear. Each changed state is reused on a subsequent build.

The overload regression introduces competing Servlet providers. With capabilities configured, `migrate()` selects the embedded provider. The String and same-project `Configuration` migration overloads leave the conflict unresolved until the caller explicitly installs capability resolution. Both then transform the legacy dependency and replay successfully from cache. This preserves their deliberate semantic difference.

Publishing checks compare actual POM text, Gradle module metadata, reproducible JAR bytes, outgoing-variant reports, local runtime resolution and project-consumer outputs between ordinary and isolated builds, and again after cache reuse. Only the invocation-specific metadata build ID and fixture path are normalized. The assertions preserve declared dependencies in publications even when local resolution substitutes/transforms them. Automatic migration advertises the existing migration attribute on `runtimeElements`, while `apiElements` remains unmarked; runtime-only migration does not mark outgoing variants. Published project resources remain unchanged. These checks cover `maven-publish` with the Java component; Ivy and other publishing plugins are outside this scope.

On Gradle 8.6, creating a Maven publication in a child project reports `Cannot access project ':' from project ':library'` and discards the configuration cache under strict isolation. A minimal control reproduces this using only Gradle's Java Library and Maven Publish plugins. The characterization repeats it with the Jakarta plugin and automatic migration enabled. Ordinary publishing stores/reuses the cache in both cases. This is a reproduced Gradle publishing limitation, so the passing 8.6 migration matrix does not establish multi-project publishing isolation support. No plugin workaround or production change is introduced.

## Support decisions still open

Gradle 4.0 cannot apply the current implementation: extension creation fails with `NoClassDefFoundError` for Gradle provider APIs. An empty Gradle 4.0 fixture also rejects `--configuration-cache` and silently ignores the unsafe isolation property. The README removes the obsolete positive claim and records this evidence without selecting a new implementation minimum.

Maintainers still need to choose a supported ordinary-build minimum and decide whether to restore older compatibility or formally change policy through the release process. They also need to decide whether the 8.6 publishing limitation excludes that workflow or requires a higher declared isolation-support baseline. Passing the pinned versions does not certify intermediate versions or other 9.8 patch releases. Full CI coverage on the repository's JDK 17/21/25 test-worker matrix remains separate from this local JDK 21 run; the feature fixtures deliberately use JDK 21 on all hosts. The stable isolation CLI flags are verified on 9.8.2; Gradle 9.7 is outside this matrix.

Arbitrary cross-project mutable-state customizations remain outside the isolation contract. These bounded tests do not establish logging-race freedom for the shared Tomcat JUL handler, compatibility of reflective JAXB internals across other Gradle versions, or support for every integration.
