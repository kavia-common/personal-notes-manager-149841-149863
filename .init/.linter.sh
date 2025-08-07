#!/bin/bash
cd /home/kavia/workspace/code-generation/personal-notes-manager-149841-149863/notes_android_app
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

