#!/bin/bash

# Script pour connecter le dépôt local à un dépôt distant
# Usage: ./connect_remote.sh <URL_DU_DEPOT>

set -e

REPO_URL="$1"

if [ -z "$REPO_URL" ]; then
    echo "❌ Erreur: URL du dépôt requise"
    echo ""
    echo "Usage: ./connect_remote.sh <URL_DU_DEPOT>"
    echo ""
    echo "Exemples:"
    echo "  ./connect_remote.sh https://github.com/Su-Senders/hrms-platform-backend.git"
    echo "  ./connect_remote.sh git@github.com:Su-Senders/hrms-platform-backend.git"
    echo ""
    exit 1
fi

echo "🔗 Connexion au dépôt distant..."
echo "URL: $REPO_URL"
echo ""

# Vérifier si un remote existe déjà
if git remote get-url origin >/dev/null 2>&1; then
    echo "⚠️  Un remote 'origin' existe déjà:"
    git remote -v
    echo ""
    read -p "Voulez-vous le remplacer? (y/N): " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        git remote remove origin
        echo "✅ Remote 'origin' supprimé"
    else
        echo "❌ Opération annulée"
        exit 1
    fi
fi

# Ajouter le remote
echo "➕ Ajout du remote 'origin'..."
git remote add origin "$REPO_URL"

# Vérifier la connexion
echo ""
echo "🔍 Vérification de la connexion..."
git remote -v

# Tester la connexion (sans pousser)
echo ""
echo "🧪 Test de connexion au dépôt distant..."
if git ls-remote origin >/dev/null 2>&1; then
    echo "✅ Connexion réussie!"
    echo ""
    echo "📊 Informations du dépôt distant:"
    git ls-remote --heads origin | head -5
    echo ""
    
    # Vérifier si le dépôt distant a du contenu
    REMOTE_MAIN=$(git ls-remote --heads origin main 2>/dev/null | wc -l | tr -d ' ')
    
    if [ "$REMOTE_MAIN" -gt 0 ]; then
        echo "⚠️  Le dépôt distant contient déjà une branche 'main'"
        echo ""
        read -p "Voulez-vous fusionner les historiques? (y/N): " -n 1 -r
        echo ""
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            echo "🔄 Fusion des historiques..."
            git pull origin main --allow-unrelated-histories --no-edit
            echo "✅ Fusion réussie"
        else
            echo "ℹ️  Vous devrez gérer les conflits manuellement"
        fi
    fi
    
    echo ""
    echo "🚀 Prêt à pousser le code!"
    echo ""
    echo "Pour pousser le code, exécutez:"
    echo "  git push -u origin main"
    echo ""
else
    echo "❌ Échec de la connexion"
    echo ""
    echo "Vérifiez:"
    echo "  1. L'URL du dépôt est correcte"
    echo "  2. Vous avez les permissions d'accès"
    echo "  3. Votre authentification est configurée (token SSH/HTTPS)"
    exit 1
fi

