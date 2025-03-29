#!/bin/bash

# Ensure we're in the project root
cd "$(dirname "$0")"

# Pull latest from our main branch in the fork
git pull origin main

# Runs a gradle task to generate Dokka docs
yarn dokka

# Copy docs to the docs/dokka folder
yarn copy:dokka

# Add the docs to the commit
git add docs/

# Commit changes, with current date in format [DD-MM-YYYY]
git commit -m "[$(date +%d-%m-%Y)]"

git push origin docs/dokka

echo "Dokka docs updated and pushed!"
