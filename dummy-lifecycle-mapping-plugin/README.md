OpenGamma M2E lifecycle mapping hack
------------------------------------
This is a hack to remove a warning from the command line build caused by m2e.
It simply allows a placeholder jar to be added to the OpenGamma repository that blocks the warnings.
Without this dummy plugin, everyone sees this message:

```
[WARNING] The POM for org.eclipse.m2e:lifecycle-mapping:jar:1.0.0 is missing, no dependency information available
[WARNING] Failed to retrieve plugin descriptor for org.eclipse.m2e:lifecycle-mapping:1.0.0: Plugin org.eclipse.m2e:lifecycle-mapping:1.0.0 or one of its dependencies could not be resolved: Failed to read artifact descriptor for org.eclipse.m2e:lifecycle-mapping:jar:1.0.0
```

See the [GitHub project](https://github.com/mfriedenhagen/dummy-lifecycle-mapping-plugin)
and the [Stack Overflow](http://stackoverflow.com/questions/7905501/get-rid-of-pom-not-found-warning-for-org-eclipse-m2elifecycle-mapping/) question.

NOTE: This project is publicly available.

[![OpenGamma](http://developers.opengamma.com/res/display/default/chrome/masthead_logo.png "OpenGamma")](http://developers.opengamma.com)
