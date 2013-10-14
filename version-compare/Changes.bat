@echo off
call japi-compliance-checker -show-access -skip-deprecated -lib util old-util.xml new-util.xml
call japi-compliance-checker -show-access -skip-deprecated -lib db old-db.xml new-db.xml
call japi-compliance-checker -show-access -skip-deprecated -lib analytics old-analytics.xml new-analytics.xml
call japi-compliance-checker -show-access -skip-deprecated -lib main old-main.xml new-main.xml
call japi-compliance-checker -show-access -skip-deprecated -lib platform old-platform.xml new-platform.xml
