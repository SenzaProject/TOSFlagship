#!/data/data/com.termux/files/usr/bin/bash
# Jalankan dari dalam folder proyek:  bash push.sh
set -e
git config --global user.name "SenzaProject"
git config --global user.email "dionoberutu06@gmail.com"

[ -d .git ] || git init -b main
git add -A
git commit -m "TOS Flagship Update-4.1: APK app + logo" || true
git branch -M main
git remote remove origin 2>/dev/null || true
git remote add origin https://github.com/SenzaProject/TOSFlagship.git
echo "Saat diminta password, tempel Personal Access Token GitHub (bukan password akun)."
git push -u origin main
