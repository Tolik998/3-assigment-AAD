#!/bin/bash

echo "=== City Transportation Network Optimization ==="
echo "Building and running the project..."

# Create directories if they don't exist
mkdir -p data

# Compile and run
mvn compile

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"

    # Check if we should generate new test data
    if [ "$1" == "--generate" ] || [ ! -f "data/input.json" ]; then
        echo "📁 Generating test data..."
        mvn exec:java -Dexec.mainClass="com.transportation.Main" -Dexec.args="--generate" -q
    fi

    echo "🔍 Running analysis..."
    mvn exec:java -Dexec.mainClass="com.transportation.Main" -q

    echo "🧪 Running tests..."
    mvn test

    echo "📚 Generating documentation..."
    mvn javadoc:javadoc

    echo ""
    echo "🎉 All tasks completed successfully!"
    echo "📊 Results: data/output.json"
    echo "📖 Documentation: target/site/apidocs/index.html"
    echo "📋 Test reports: target/surefire-reports/"
else
    echo "❌ Compilation failed!"
    exit 1
fi