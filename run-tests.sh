#!/bin/bash

# Script pour exécuter les tests
# Usage: ./run-tests.sh [options]

set -e

echo "🧪 Exécution des tests HRMS Platform"
echo ""

# Vérifier si Maven est disponible
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé ou n'est pas dans le PATH"
    echo ""
    echo "Options:"
    echo "  1. Installer Maven: https://maven.apache.org/download.cgi"
    echo "  2. Utiliser un IDE (IntelliJ, Eclipse, VS Code)"
    echo "  3. Utiliser Docker avec Maven"
    exit 1
fi

# Options par défaut
TEST_PROFILE="test"
SKIP_TESTS=false
TEST_CLASS=""

# Parser les arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --skip-compile)
            SKIP_COMPILE=true
            shift
            ;;
        --test-class)
            TEST_CLASS="$2"
            shift 2
            ;;
        --help)
            echo "Usage: ./run-tests.sh [options]"
            echo ""
            echo "Options:"
            echo "  --skip-compile    Ne pas compiler avant les tests"
            echo "  --test-class      Exécuter une classe de test spécifique"
            echo "  --help            Afficher cette aide"
            echo ""
            echo "Exemples:"
            echo "  ./run-tests.sh"
            echo "  ./run-tests.sh --test-class GeographicServiceTest"
            echo "  ./run-tests.sh --test-class '*ServiceTest'"
            exit 0
            ;;
        *)
            echo "Option inconnue: $1"
            echo "Utilisez --help pour voir les options disponibles"
            exit 1
            ;;
    esac
done

# Compiler le projet si nécessaire
if [ "$SKIP_COMPILE" != "true" ]; then
    echo "📦 Compilation du projet..."
    mvn clean compile test-compile
    echo "✅ Compilation terminée"
    echo ""
fi

# Exécuter les tests
echo "🧪 Exécution des tests..."
echo ""

if [ -n "$TEST_CLASS" ]; then
    echo "Exécution de la classe: $TEST_CLASS"
    mvn test -Dtest="$TEST_CLASS" -Dspring.profiles.active=$TEST_PROFILE
else
    echo "Exécution de tous les tests..."
    mvn test -Dspring.profiles.active=$TEST_PROFILE
fi

echo ""
echo "✅ Tests terminés !"
echo ""
echo "📊 Pour voir le rapport de couverture:"
echo "  mvn jacoco:report"
echo "  open target/site/jacoco/index.html"

