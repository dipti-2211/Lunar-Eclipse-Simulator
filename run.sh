#!/usr/bin/env bash
set -e
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

echo "============================================="
echo "   Interactive Lunar Eclipse Simulator      "
echo "============================================="

if ! command -v javac &> /dev/null || ! command -v java &> /dev/null; then
    echo "Error: Java Development Kit (JDK) is not found in PATH."
    echo "Please ensure JDK is installed and available."
    exit 1
fi

echo "Compiling LunarEclipseApplet..."
javac -d . src/LunarEclipseApplet.java

echo "Launching Lunar Eclipse Simulator Desktop Window..."
java LunarEclipseApplet

