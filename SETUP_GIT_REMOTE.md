# Configuration du Dépôt Git Distant

**Date** : Configuration du remote Git  
**Statut** : Dépôt local initialisé, prêt pour connexion distante

---

## 📊 État Actuel

✅ **Dépôt Git local** : Initialisé  
✅ **Commit initial** : Créé (1c9feac)  
✅ **Branche principale** : `main`  
❌ **Remote** : Non configuré

---

## 🔗 Options pour Connecter le Dépôt

### Option 1 : GitHub (Recommandé)

#### Étape 1 : Créer un nouveau dépôt sur GitHub

1. Aller sur [GitHub](https://github.com)
2. Cliquer sur "New repository"
3. Nom suggéré : `hrms-platform-backend` ou `hrms-minat-backend`
4. **Ne pas** initialiser avec README, .gitignore ou licence (déjà présents)
5. Copier l'URL du dépôt (ex: `https://github.com/VOTRE_USERNAME/hrms-platform-backend.git`)

#### Étape 2 : Ajouter le remote et pousser

```bash
cd /Users/stephanelounga/hrms-platform/backend

# Ajouter le remote
git remote add origin https://github.com/VOTRE_USERNAME/hrms-platform-backend.git

# Vérifier la connexion
git remote -v

# Pousser le code
git push -u origin main
```

---

### Option 2 : GitLab

#### Étape 1 : Créer un nouveau dépôt sur GitLab

1. Aller sur [GitLab](https://gitlab.com)
2. Créer un nouveau projet
3. Nom suggéré : `hrms-platform-backend`
4. **Ne pas** initialiser avec README
5. Copier l'URL du dépôt

#### Étape 2 : Ajouter le remote et pousser

```bash
cd /Users/stephanelounga/hrms-platform/backend

# Ajouter le remote
git remote add origin https://gitlab.com/VOTRE_USERNAME/hrms-platform-backend.git

# Vérifier la connexion
git remote -v

# Pousser le code
git push -u origin main
```

---

### Option 3 : Dépôt Existant

Si vous avez déjà un dépôt distant :

```bash
cd /Users/stephanelounga/hrms-platform/backend

# Ajouter le remote existant
git remote add origin <URL_DU_DEPOT_EXISTANT>

# Vérifier la connexion
git remote -v

# Pousser le code (peut nécessiter --force si le dépôt distant a déjà du contenu)
git push -u origin main
```

---

## ✅ Vérification de la Connexion

### Commandes de Vérification

```bash
# Vérifier les remotes configurés
git remote -v

# Tester la connexion (sans pousser)
git ls-remote origin

# Vérifier le statut
git status

# Voir les branches
git branch -a
```

---

## 🔐 Authentification

### GitHub

**Option A : HTTPS avec Token**
```bash
# Créer un Personal Access Token sur GitHub
# Settings > Developer settings > Personal access tokens > Tokens (classic)
# Permissions : repo (toutes)

# Utiliser le token comme mot de passe lors du push
git push -u origin main
# Username: VOTRE_USERNAME
# Password: VOTRE_TOKEN
```

**Option B : SSH**
```bash
# Générer une clé SSH si nécessaire
ssh-keygen -t ed25519 -C "stesenders@gmail.com"

# Ajouter la clé à GitHub
# Settings > SSH and GPG keys > New SSH key

# Utiliser l'URL SSH
git remote set-url origin git@github.com:VOTRE_USERNAME/hrms-platform-backend.git
```

### GitLab

Similaire à GitHub, avec Personal Access Token ou SSH.

---

## 🚀 Commandes Rapides

### Script Complet (à adapter avec votre URL)

```bash
#!/bin/bash
cd /Users/stephanelounga/hrms-platform/backend

# Remplacer par votre URL
GIT_REPO_URL="https://github.com/VOTRE_USERNAME/hrms-platform-backend.git"

# Ajouter le remote
git remote add origin $GIT_REPO_URL

# Vérifier
git remote -v

# Pousser
git push -u origin main
```

---

## 📝 Notes Importantes

1. **Ne pas pousser les secrets** : Le `.gitignore` exclut déjà `application-secrets.properties`
2. **Premier push** : Utiliser `-u` pour définir le tracking de la branche
3. **Conflits** : Si le dépôt distant a du contenu, vous devrez peut-être faire un `git pull --rebase` d'abord
4. **Branche principale** : Le dépôt utilise `main` (standard moderne)

---

## 🔍 Dépannage

### Erreur : "remote origin already exists"
```bash
# Supprimer le remote existant
git remote remove origin

# Réajouter
git remote add origin <URL>
```

### Erreur : "failed to push some refs"
```bash
# Si le dépôt distant a du contenu
git pull origin main --allow-unrelated-histories

# Puis pousser
git push -u origin main
```

### Erreur d'authentification
- Vérifier les credentials dans `~/.git-credentials`
- Utiliser un Personal Access Token au lieu du mot de passe
- Vérifier les permissions du token

---

**Prêt pour la connexion !** 🚀

