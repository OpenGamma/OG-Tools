Releasing og-platform-project
--------------------------
The pom cannot contain the distributionManagement block, so the relevant command is stored here

mvn clean deploy -DaltDeploymentRepository=og-public-release::default::${og-public-release.url}
