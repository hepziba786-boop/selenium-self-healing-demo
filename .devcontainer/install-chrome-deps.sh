#!/usr/bin/env bash
# Installs the Linux shared libraries headless Chrome (downloaded by Selenium Manager) needs to launch.
set -euo pipefail

sudo apt-get update
sudo apt-get install -y \
  libatk1.0-0 libatk-bridge2.0-0 libgtk-3-0 libnss3 libxss1 \
  libasound2t64 libgbm1 libxshmfence1 libxcomposite1 libxdamage1 \
  libxrandr2 libcups2 libdrm2 libxkbcommon0 fonts-liberation xdg-utils
