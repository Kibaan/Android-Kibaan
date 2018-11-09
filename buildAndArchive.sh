#!/bin/sh

echo "==========[Kibaan] Build START. =========="

./gradlew kibaan:assembleRelease

echo "==========[Kibaan] Build END. ============"

echo "==========[Kibaan] Archive START. ========"

./gradlew uploadArchives

echo "==========[Kibaan] Archive END. =========="