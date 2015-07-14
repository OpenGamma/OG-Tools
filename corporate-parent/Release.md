Releasing corporate-parent
--------------------------
The pom cannot contain the distributionManagement block, so the relevant command is stored here

mvn clean deploy -Doss.repo -Dgpg.passphrase=real-password-needed
