#!/bin/bash

if [ -f .env ]; then

    while IFS='=' read -r key value; do
        [[ -z "$key" || "$key" =~ ^[[:space:]]*# ]] && continue

        key=$(echo "$key" | xargs)
        value=$(echo "$value" | xargs)

        export "$key=$value"
    done < .env
fi

./gradlew bootRun
